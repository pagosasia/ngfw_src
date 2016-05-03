{
    "uniqueId": "virus-blocker-U9q10lbp",
    "category": "Virus Blocker",
    "description": "The number of clients with blocked viruses by web activity.",
    "displayOrder": 105,
    "enabled": true,
    "javaClass": "com.untangle.node.reports.ReportEntry",
    "orderByColumn": "value",
    "orderDesc": true,
    "units": "hits",
    "pieGroupColumn": "c_client_addr",
    "pieSumColumn": "count(*)",
    "conditions": [
        {
            "column": "virus_blocker_clean",
            "javaClass": "com.untangle.node.reports.SqlCondition",
            "operator": "=",
            "value": "false"
        }
    ],
    "readOnly": true,
    "table": "http_events",
    "title": "Web Top Blocked Clients",
    "pieStyle": "PIE",
    "type": "PIE_GRAPH"
}
