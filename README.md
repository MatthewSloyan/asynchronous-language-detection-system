## An Asynchronous Language Detection System – Matthew Sloyan G00348036 

For this project I made various decisions from brainstorming, writing it out on paper and trial and error. I also used the lecture notes for guidance regarding design principles and patterns, loose-coupling, high cohesion, abstraction, encapsulation, composition and inheritance. Some of the design decisions and a guide for each feature be found below under their relevant headings.

### To run and deploy the application.
* Install a Tomcat 9 server and extract the zipped file.
* Place the ngrams.war file in the webapps folder.
* Once deployed run “startup.bat”, this will open a console and deploy the .war file.
* Enter either 1 or 2 into the console (1 = Read from File & 2 = Read from URL).
* Menu option 1 - Enter the full path to the WiLi text file. An example of this would be placing this folder in the root of the C drive and entering the following path “C:/data/wili-2018-Edited.txt”. I have added this feature as an extra to allow compatibility with all platforms easily (Windows, Mac & Linux), and so the WiLi file can be place securely.
* Menu option 2 - Another option is to read in from a URL. Enter the URL to a raw text file of the WiLi dataset, which will check if it exists. I added this as another extra say for example if the dataset was stored securely on a database in the cloud rather than having it locally. An example from testing to use is “https://raw.githubusercontent.com/MatthewSloyan/asynchronous-language-detection-system/master/data/wili-2018-Edited.txt?token=AGMGWHC4EB7JAYCTCHCNYC26DS6UC”
* The application now will be available here “http://localhost:8080/ngrams/”

### Design Patterns and Principles
#### Singleton
I have implemented a Singleton pattern in the Database as there should only be one instance of it, this would allow multiple servlets to access the same database easily. I have also done the same for the JobProcessor and JobProducer which handle the in and out queue. Again, as there should only be one in and out queue across all clients this will stop accidental new instances of the queues being created.
#### Proxy
I have decided to implement a proxy to access the database, as this allows the developer to control the database service without the client knowing as the proxy is implemented as a layer of encapsulation in front of it. The proxy also works even if database isn’t ready.
#### Template
The template pattern is used in the ServiceHandler callback method init().
#### Single Responsibility Principle (SRP)
At all levels throughout the design SRP is upheld as I tried to give every class a specific purpose that delegates to other classes.
#### Open Close Principle (OCP)
Seen in the proxy implemented for the database. You can introduce new proxies without changing the real database or client. You can also make changes to the database or add new databases without affecting the proxy or the client.
#### Dependency Inversion Principle
No higher-level module depends on a low-level module. Both interfaces implemented are by classes at the same level and below the interface.
#### Interface Segregation Principle
No class implements interface methods that it doesn’t use. For example both the FileParser and Predict language class us getKmers() and getKmer().
#### Law of Demeter
No single function knows the whole navigation structure of the system. I have tried to subjugate all functionality into multiple methods. For example in the FileProcessor the run() method calls the parseLine() method.
 
## How it works:
### UI:
#### Console UI:
When running initially you will be presented with the option to enter whether you’d like to read the subject file from a file or a url. 1 = File & 2 = URL. If selecting 1 the full path including the .txt file is required. If selecting 2 the URL must be a raw text file of the WiLi dataset possibly on a database.
Once it is correct the system begins adding to a rapidly searchable structure for future queries by clients. How this works is described below. 
#### Client UI:
In a browser on “http://localhost:8080/ngrams/” the user can input a language query, this will be added to inQueue in the JobProducer. The JobProcesor will take from this queue and process this request adding it to the outQueueMap for the client to retrieve and display the result.

### ServiceHandler Class:
Overall it manages all aspects of the application, from setting up the database and making requests. Each of these processes is delegated to further classes to uphold SRP. So, all that’s contained in the ServiceHandler is the init, doGet and doPost methods. In init the database is initialised through the InitialiseDatabase class which runs a thread to parse the subject file and add to the database quickly.
### InitialiseDatabase Class: 
Called on init of application. First it calls a method to get the full file path of the WiLi text file, then it runs a thread of the Parser class to parse this file line by line. Essentially it works as a middleman between the classes abstracting the functionality from the ServiceHandler to uphold SRP.
### Parser Class:
Runnable thread that uses a file (Wili) and parses it line by line. Each line is split into the text and the language name at the @ delimiter. Using an ExecutorService a thread pool is created, and each line is processed by the FileProcessor as a single runnable thread. The thread then waits till all threads in the ES are completed as it was causing quite a few problems if let run concurrently. For simplicity and SRP this class only parses the files and handles the ES. I also tried to get the application working with a BlockingQueue where each line would be parsed, then each Kmer would be added to queue and a separate class would take each kmer off the queue and add to the database. This worked but it was considerably slower than the approach below due to the number of lines in the text file and number of possible kmers (1-4) on each line.

### FileProcessor Class:
Runnable thread that is spawned from a Thread Pool in Parser class. The line is already split into the text and the language name at the @ delimiter. This class processes a single line and adds all the possible Kmers (1-4) to the database. I did at one stage have this working with a queue where each Kmer was being added to a queue here, but as previously mentioned it added complexity and slowed down the processing considerably. Getting the kmers is done by adding each specific length kmer to the Database through the Proxy. So, if i is 2 in getKmer(), it gets every kmer length 2 in the string tiling one character at a time. I initially had it working with tiling with just length 4 kmers which was in the supplied video, however I decided to implement my own version taking from this to allow for kmers 1-4 while also tiling. This is slower than getting one after the other but it produces a larger and more accurate result. A CharSequence is also used as it uses less memory than a String. I have also removed passing through the whole Language instance here and instead a single string which saves space. The language is then checked at the database level, so only one object is created rather than two. This class also implements the Processable interface and it shares the same methods with the PredictLanguage class.

### DatabaseProxy Class:
I have decided to implement a proxy to access the database. This allows the developer to control the database service without the client knowing as the proxy is implemented as a layer of encapsulation in front of it. The proxy works even if database isn’t ready or is not available. Also upholds OCP as mentioned above. To achieve this, I have composed the real database as an instance variable, and then delegate the calls to the real database which will stores the kmers. When the constructor is called, it gets the only instance of the Database so data is consistent.

### Database Class:
I decided there should only be one instance of this, as then it can be used across any number of servlets once it's set up. This database could be placed on a server (VM) easily and implements Databaseable which could be used with other types of databases. To achieved this, I implemented a Singleton design pattern which works in a threaded environment with a double checking lock to reduce overhead. I have used a ConcurrentHashMap for the local db as it's the fastest type (search, insert and delete functions are O(1)). Also, it allows for concurrency between multiple threads as a Thread Pool is used to add Kmers.
Multiple methods are included which can be found below. 
* add() - Which adds Kmers to the local db in O(1) time. 
* resize() -  Resizes the every language in local db to the top 300 kmers (max). It first sorts the inner map, and overwrites the current map with a shortened map which is also ranked in descending order of frequency of kmer occurrence.
* getLanguage() – Used to predict the correct language using the out of place distance metric. I wanted to abstract this to the PredictLanguage class but then a getDb would be needed and that would break the encapsulation of the local ConcurrentHashMap.

### Language, LanguageEntry, OutOfPlaceMetric Classes:
* Language - Supplied enum class that contains reference to all languages and names.
* LanguageEntry - Object instance added to local db which contains kmer as a hashcode, the frequency of occurrence and the rank.
* OutOfPlaceMetric – Used and added to a ConcurrentSkipListSet which is ordered by the lowest distance.

### JobProducer & JobProcessor Classes:
JobProducer - This class processes the requests and adds them to the inQueue for the JobProcessor to process. SRP is upheld as all it does is add to the queue, and start the JobProcessor running if it's not already. I decided there should only be one instance of this, as there should only ever be one in and out queue. To achieved this I implemented a Singleton design pattern. This queue could be used for different type of jobs as it just contains a String key and String object. I have used a BlockingQueue as the loop in the JobProcessor to take from the queue will only run when there is jobs in the queue. Also, it will block automatically and wait if too many jobs are processed at once.
JobProcessor - This class processes the jobs on the inQueue which is added by the JobProducer. It also calls the PredictLanguage() class which returns the predicted language to be added to the outqueue. I decided there should only be one instance of this, as there should only ever be one in and out queue. To achieved this, I implemented a Singleton design pattern. This queue could be used for different type of jobs as it just contains a String key and String object. I have used a ConcurrentHashMap for the outQueueMap as it's the fastest type (search, insert and delete functions are O(1)). Also, it allows for concurrency between multiple threads as a thread is running to check process jobs in the queue.

### PredictLanguage Class:
Class that processes the query text from the client and returns a result. Also implements Processable which is used in the FileProcessor class it contains the same method names and signatures but different implementation. Even though it contains similar methods to the Databaseable they include different parameters and it would also break the Dependency Inversion Principle. The query string entered by the user is parsed in the same way as the Database E.g Kmers 1-4 tiled. This map is then passed to the Database to get the out of place distance metric which returns the correct language.

### Utilities Class:
Utilities methods which are abstracted and implemented from both the PredictLanguage and FileParser class, to cut down on repeated code (DRY).

## Speed tests (I7 Processor):
* Parse WiLi text file with 1-4 kmers with tiling and add to db on startup = 2.1s
* Language query from client = 0.1s

## Sample Tests 
From testing it was found results can vary, but if given enough text it is quite accurate. Below are some sample tests.
* Spanish - Hola! Yo empecé aprendo Español hace dos mes en la escuela. Yo voy la universidad. Yo tratar estudioso Español tres hora todos los días para que yo saco mejor rápido. ¿Cosa algún yo debo hacer además construir mí vocabulario? Muchas veces yo estudioso la palabras solo para que yo construir mí voabulario rápido.
* Irish - Dia Duit! Conas atá tú? Is breá liom bheith ag obair le madraí agus cait. Go raibh maith agat as labhairt liom. Is maith liom freisin féachaint ar scannáin agus ar chluichí le cairde chomh maith le bheith ag éisteacht le ceol ag an deireadh seachtaine.
* French - Le lundi matin, mon père travaille au bureau, ma mère reste à la maison, ma petite sœur va à l'école, et je vais à l'université. Le mardi, le mercredi, le jeudi, et finalement le vendredi, nous faisons la même chose. Mais le week-end, il est assez différent. Pendant le week-end, nous ne sommes pas très occupés comme les autres jours. Le samedi matin, mon père qui est très sportif fait de la natation, et ma mère fait la cuisine parce que chaque samedi, mes parents invitent ma tante à dîner avec nous. Enfin, le dimanche d'habitude nous ne faisons pas grand-chose; quelques fois, mon père fait du bricolage si nécessaire.

## Additions/Extras:
* The option to enter the full path to the WiLi text file to allow compatibility with all platforms easily (Windows, Mac & Linux), also so it can be placed securely.
* The option to read the WiLi text file from a URL rather than locally if it was stored on a database securely in the cloud.
* Tiling implemented for Kmers of length 1-4 which produces a larger database and more accurate result.
* Design patterns such as Singletons and a Proxy implemented to improved overall design and reuse.
* I modified the web application to check for a result every 2 seconds, so the user gets the result quickly, and the predicted resulted is stored on the page as a hidden variable so it doesn’t clear. Also added a “New Query” button to allow the user to go back to the home page.
* Thread pools and ConcurrentHashMap implementation for speed on file parsing.
* Implemented hashcodes on all kmers and tried using encoded longs.
* Validation on user inputs especially on the file path and URL to check if they exist.

