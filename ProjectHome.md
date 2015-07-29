# Alfresco Bulk Export Tool #

A bulk filesystem export tool for the open source [Alfresco](http://www.alfresco.com) CMS.

This module has as main objective provides a simple way to export Alfresco storaged content (with properties) to a file system.  To start this system is used a simple HTTP/GET call to a webscript that just can be initialized by system administrator.

The format of filesystem choosed is described by Alfresco Bulk filesystem import, that can be found in [Bulk Import Project Page](http://code.google.com/p/alfresco-bulk-filesystem-import/).

A smal detail that need to be mencioned is the fact that file exportion will be done in server machine (even called by remote machine), else,  you also can map a remote directory in the server end export content to there.

Please don't hesitate to contact the project owner if you'd like to contribute!