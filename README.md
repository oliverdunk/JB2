## JB2
**B2 File versions are currently not supported!**

JB2 is a Java implementation of the Backblaze [B2](https://www.backblaze.com/b2/cloud-storage.html) cloud storage solution.
It is licensed under the [MIT License], and built using Java 8.

## Prerequisites
* Java 8
* A Backblaze B2 account (free)

## Getting Started
Using JB2 is made to be as simple as possible.
To get started, you simply need to create a new session using the ```B2API``` class.
```
B2Session session = B2API.authorizeAccount("accountID", "applicationKey");
```
You can then create a ```B2Bucket```, and upload files to it using the following:
```
//Create the Bucket
B2Bucket bucket = B2API.createBucket(session, "ExampleBucket", BucketType.ALL_PRIVATE);

//Retrieve an upload URL
B2UploadRequest request = B2API.getUploadURL(session, bucket);

//Upload the file
B2File file = B2API.uploadFile(request, new File("test.txt"), "test.txt");
```

Further API methods are avaliable in the ```B2API``` class, and the JavaDoc comments explain what each is used for. 

## Contributing
We welcome all contributions! Simply fork this project, and get started. Make sure to make all your changes on a seperate branch of your fork, and the commit changes when ready.

The only rule is that if you make multiple commits for one PR, please use the 'git rebase' command to squash all of the commits into one.

[MIT License]: http://www.tldrlegal.com/license/mit-license