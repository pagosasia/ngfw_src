{
    "uniqueId": "firewall-cnxNJ0ZXsz",
    "category": "Firewall",
    "description": "The number of flagged session grouped by username.",
    "displayOrder": 602,
    "enabled": true,
    "javaClass": "com.untangle.node.reporting.ReportEntry",
    "orderByColumn": "value",
    "orderDesc": true,
    "units": "hits",
    "pieGroupColumn": "username",
    "pieSumColumn": "coalesce(sum(firewall_blocked::int),0)",
    "preCompileResults": false,
    "readOnly": true,
    "table": "sessions",
    "title": "Top Blocked Usernames",
    "type": "PIE_GRAPH"
}
