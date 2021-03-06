"""
Intrusion Prevention Rule
"""
import re

Rule_globals = None
#Rule_system_memory=None
class IntrusionPreventionRule:

    global_values = None

    reserved_id_regex = re.compile(r'^reserved_')

    def __init__(self, settingsRule):
        self.rule = settingsRule

        if IntrusionPreventionRule.global_values is None:
            IntrusionPreventionRule.build_global_values()

    @staticmethod
    def build_global_values():
        IntrusionPreventionRule.global_values = {}
        meminfo = open( "/proc/meminfo" )
        for line in meminfo:
            if "MemTotal:" in line:
                value = re.split(' +', line)[1]
                IntrusionPreventionRule.global_values["SYSTEM_MEMORY"] = int(value) * 1024

    def get_enabled(self):
        return self.rule["enabled"]

    # def get_id(self):
    #     return self.rule["id"]

    def matches(self, signature):
        if self.rule["enabled"] == False:
            return False

        # print(self.rule["conditions"])
        match = True
        for condition in self.rule["conditions"]["list"]:
            # print("condition")
            # print(condition)

            comparator = condition['comparator']
            targetValue = condition["value"]

            if condition["type"] == "SID":
                match = self.matches_numeric(int(signature.options["sid"]), comparator, targetValue)
            elif condition["type"] == "GID":
                match = self.matches_numeric(int(signature.options["gid"]), comparator, targetValue)
            elif condition["type"] == "ID":
                conditionArgs["actualValue"] = signature.signature_id
                match = self.matches_numeric(signature.signature_id, comparator, targetValue)
                #comparator: 'numeric'
            elif condition["type"] == "CATEGORY":
                if not isinstance(condition["value"],list):
                    condition["value"] = condition["value"].split(',')
                match = self.matches_in(signature.category, comparator, condition["value"])
            elif condition["type"] == "CLASSTYPE":
                if not isinstance(condition["value"],list):
                    condition["value"] = condition["value"].split(',')
                match = self.matches_in(signature.options["classtype"], comparator, condition["value"])
            elif condition["type"] == "MSG":
                match = self.matches_text(signature.options["msg"], comparator, targetValue)
            elif condition["type"] == "PROTOCOL":
                if not isinstance(condition["value"],list):
                    condition["value"] = condition["value"].split(',')
                match = self.matches_in(signature.protocol, comparator, condition["value"])
            elif condition["type"] == "SRC_ADDR":
                conditionArgs["actualValue"] = signature.lnet
                conditionArgs["comparatorType"] = "network"
            elif condition["type"] == "SRC_PORT":
                match = self.matches_numeric(int(signature.lport), comparator, targetValue)
            elif condition["type"] == "DST_ADDR":
                conditionArgs["actualValue"] = signature.rnet
                conditionArgs["comparatorType"] = "network"
            elif condition["type"] == "DST_PORT":
                conditionArgs["actualValue"] = signature.rport
                match = self.matches_numeric(int(signature.rport), comparator, targetValue)
            elif condition["type"] == "RULE":
                conditionArgs["actualValue"] = signature.options_raw
                conditionArgs["comparatorType"] = "text"
            elif condition["type"] == "SOURCE":
                conditionArgs["actualValue"] = signature.path
                conditionArgs["comparatorType"] = "text"
            elif condition["type"] == "SYSTEM_MEMORY":
                match = self.matches_numeric(IntrusionPreventionRule.global_values["SYSTEM_MEMORY"], comparator, targetValue)
            else:
                ### exception
                print("UNKNOWN")
                match = False
                break

            # print("match result=")
            # print(match)

            if match is False:
                return False

        return match

    def matches_numeric(self, sourceValue, comparator, targetValue):
        if comparator == "=":
            return sourceValue == targetValue
        elif comparator == "!=":
            return sourceValue != targetValue
        elif comparator == "<=":
            return sourceValue <= targetValue
        elif comparator == "<":
            return sourceValue < targetValue
        elif comparator == ">":
            return sourceValue > targetValue
        elif comparator == ">=":
            return sourceValue >= targetValue

        return False

    def matches_text(self, sourceValue, comparator, targetValue):
        if comparator == "=":
            return sourceValue == targetValue
        elif comparator == "!=":
            return sourceValue != targetValue
        elif comparator == "substr":
            return targetValue in targetValue
        elif comparator == "!substr":
            return not targetValue in targetValue

        return False

    def matches_in(self, sourceValue, comparator, targetValue):
        is_in = sourceValue in targetValue

        if comparator == "=":
            return is_in
        elif comparator == "!=":
            return not is_in

        return False

    def set_signature_action(self, signature):
        current_action = signature.get_action()

        modified = str(current_action) == str(signature.initial_action)
        # set rule_action_applied field in signature.
        # if block set, don't do anything else.

        ## precidence:
        # default
        # log
        # block
        # disabled

        if self.rule["action"] == "default":
            signature.set_action(current_action["log"], current_action["block"])
        # elif self.rule["action"] == "log":
        elif self.rule["action"] == "log":
            # if not current_action["enabled"] and not current_action["log"]
            # print("set log")
            signature.set_action(True, current_action["block"])
        elif self.rule["action"] == "block":
            # if not current_action["enabled"] and not current_action["block"]
            # print("set block")
            signature.set_action(current_action["log"], True)
        elif self.rule["action"] == "disable":
            if not modified:
                # if not current_action["enabled"] and not current_action["block"]
                # print("set disabled")
                signature.set_action(False, False)
