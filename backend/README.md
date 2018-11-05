#### Prerequisites:
1. JAVA 8 latest SDK
2. Gradle to build project
3. Docker Tools

####  Steps to Execute Backend Services:
1. Run ElasticSearch by executing the steps below:<br/>
```
docker pull docker.elastic.co/elasticsearch/elasticsearch:6.4.0
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.4.0
```

2. Run Backend Project by executing `./gradlew bootRun` command. Then, it will be available on port `8080`. As an alternative way, you can run `./gradlew bootJar` command. Then, a single über jar will be built under `build/libs` directory. You can execute it anywhere simply running `java -jar docsearchtool-backend-xxx.jar`.  
At startup, backend will create indexes if they do not exist.

#### REST APIs:
##### 1. Index API with URLs
This API is used to index PDF files. It downloads the PDF files from input URLs.
Then indexes all PDF content for search index and table of contents part for suggestion index.
Therefore, there are two indexes in elastic search. 
One is search index (i.e. index name: `pdfs`), the other is suggest index (i.e. index name: `tocs`).

*Every item in the input JSON executed in parallel.*

The format of the input for one item is like below:
```
{
    "version":"11.2.0",
    "fileName":"CRM.Web_InstallationGuide.pdf",
    "url":"https://docs.google.com/a/aurea.com/viewer?a=v&pid=sites&srcid=YXVyZWEuY29tfGtub3dsZWRnZS1zZXJ2aWNlc3xneDo0YmZmZDU1ZWRiNzdlOTQy"
}
```
Here,
`version` shows the version of the PDF document. 
`fileName` shows the name of the document and will be shown in search results.
`url` shows the real link of the document. API will download the PDF and index it using this URL.

An example request is shown below:

```
HTTP Method: POST
Resource URL: http://localhost:8080/index
Request Body: JSON
Example:

[
	{
		"version":"11.2.0",
		"fileName":"CRM.Web_InstallationGuide.pdf",
		"url":"http://localhost:1337/CRM.Web_InstallationGuide.pdf"
	},
	{
		"version":"10.2.0",
		"fileName":"Core_AdministratorGuide.pdf",
		"url":"http://localhost:1337/Core_AdministratorGuide.pdf"
	},
	{
		"version":"11.2.0",
		"fileName":"CRM.Web_AdministratorGuide.pdf",
		"url":"http://localhost:1337/CRM.Web_AdministratorGuide.pdf"
	}
]
```

##### 2. Index API with File Uploads
This API is similar with the Index API with URLs. Only difference, it does not download PDF from the URL.
Client sends the PDF file and metadata JSON as multipart form data. This API index the file with matching metadata information in the sent JSON metadata.
Therefore, this API expects two field in multpart form data request. Those are: `files` and `metadata`. Client can add multiple files with `files` field. Then, it has to insert necessary metadata with `metadata` field.
The format of the metadata for one item is like below:
```
{
    "version":"11.2.0",
    "fileName":"CRM.Web_InstallationGuide.pdf",
    "url":"https://docs.google.com/a/aurea.com/viewer?a=v&pid=sites&srcid=YXVyZWEuY29tfGtub3dsZWRnZS1zZXJ2aWNlc3xneDo0YmZmZDU1ZWRiNzdlOTQy"
}
```
Here, 
`version` shows the version of the PDF document. 
`fileName` shows the name of the document and will be shown in search results.
`url` shows the real link of the document. It will be used in search results to redirect the user to the PDF document.

An example request is shown below:

```
HTTP Method: POST
Resource URL: http://localhost:8080/index-files
Request Type: MultiPart Form Data
Example:

Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryrvySNC2Ujtx5l0BF

------WebKitFormBoundaryrvySNC2Ujtx5l0BF
Content-Disposition: form-data; name="files"; filename="Core_AdministratorGuide_v11.2.pdf"
Content-Type: application/pdf


------WebKitFormBoundaryrvySNC2Ujtx5l0BF
Content-Disposition: form-data; name="files"; filename="Core_AdministratorGuide.pdf"
Content-Type: application/pdf


------WebKitFormBoundaryrvySNC2Ujtx5l0BF
Content-Disposition: form-data; name="files"; filename="CRM.Web_InstallationGuide.pdf"
Content-Type: application/pdf


------WebKitFormBoundaryrvySNC2Ujtx5l0BF
Content-Disposition: form-data; name="files"; filename="CRM.Web_AdministratorGuide.pdf"
Content-Type: application/pdf


------WebKitFormBoundaryrvySNC2Ujtx5l0BF
Content-Disposition: form-data; name="metadata"

[{"version":"11.2.0","fileName":"CRM.Web_InstallationGuide.pdf","url":"http://localhost:1337/CRM.Web_InstallationGuide.pdf"},{"version":"11.2.0","fileName":"CRM.Web_AdministratorGuide.pdf","url":"http://localhost:1337/CRM.Web_AdministratorGuide.pdf"},{"version":"10.2.0","fileName":"Core_AdministratorGuide.pdf","url":"http://localhost:1337/Core_AdministratorGuide.pdf"},{"version":"11.2.0","fileName":"Core_AdministratorGuide.pdf","url":"http://localhost:1337/Core_AdministratorGuide_v11.2.pdf"}]
------WebKitFormBoundaryrvySNC2Ujtx5l0BF--
```

##### 3. Search API
This API is used to search a phrase in all of PDFs' content in elastic search `pdfs` index.

```
HTTP Method: GET
Resource URL: http://localhost:8080/search?phrase=<phrase>
```

##### 4. Suggest API
This API is used to search a phrase in all of PDFs' table of contents in elastic search `tocs` index.

```
HTTP Method: GET
Resource URL: http://localhost:8080/suggest?phrase=<phrase>
```