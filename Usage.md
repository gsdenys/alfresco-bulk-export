# Export Alfresco Content #

> This module is started by a simple webscript call. To initiate the exportation you just use this URL in a browser:

> http://{host}:{port}/alfresco/service/extensions/bulkexport/export?nodeRef={noderef}&base={base}&ignoreExported={ignoreExported?}

> where:
    * **{host}:** is the host of your instalation.
    * **{port}:** is the port used by Alfresco.
    * **{noderef}:** is an Alfresco node reference that you want to export. Like: _workspace://`SpacesStore`/c494aff5-bedf-40fa-8d0d-2aebcd583579_
    * **{base}:** is a base path of your target folder (in the Alfresco Server). Like: _/home/gsdenys/export_ or _C:/export_.
    * **{ignoreExported?}:** this parameter is not required. when it is true, the sistem ignore all Alfresco nodes already exported. The default is _false_.

> When the export is ended you will see in browser a message _"Process finished Successfully"_. Once this message is printed, look-up your content in the Alfresco Server in the {base} directory.