{
    "uniqueId": "bandwidth-control-48CahLowKb",
    "category": "Bandwidth Control",
    "description": "The sum of the data transferred grouped by bypassed.",
    "displayOrder": 950,
    "enabled": true,
    "javaClass": "com.untangle.node.reports.ReportEntry",
    "orderByColumn": "value",
    "orderDesc": true,
    "units": "MB",
    "pieGroupColumn": "bypassed",
    "pieSumColumn": "round(coalesce(sum(s2p_bytes + p2s_bytes), 0) / (1024*1024),1)",
    "readOnly": true,
    "table": "sessions",
    "title": "Bypassed (by total bytes)",
    "pieStyle": "PIE",
    "type": "PIE_GRAPH"
}
