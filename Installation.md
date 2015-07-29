# Prerequisites #

Please make sure you are running a version of Alfresco that the tool has been developed for. As of the time of writing, this means either Alfresco v4.0. You also need to be running at least JDK 1.6 - the tool uses Java features that were added in this version and will not work on earlier versions.


# Installation Steps #

The following steps describe how to download and install the Alfresco Bulk Filesystem Import Tool:

  1. Download the latest AMP file containing the tool from here
  1. Shutdown your Alfresco instance
  1. Make a backup of the original alfresco.war file. On Tomcat, this is located in ${ALFRESCO\_HOME}/tomcat/webapps
  1. Use the Alfresco [Module Management Tool](http://wiki.alfresco.com/wiki/Module_Management_Tool) to install the AMP file obtained in step 1
  1. Restart Alfresco, watching the log carefully for errors
