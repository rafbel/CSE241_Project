Project developed by Rafael Cortez Bellotti de Oliveira

CSE241 Spring 2016 Project - Jog Interface System

///////////////////
/  Introduction   /
//////////////////

This project was designed for CSE241 - Data Base Systems & Apps. This course was taken at Lehigh University on the Spring semester of 2016.
The objective was "to provide a realistic experience in the conceptual design, logical design,implementation, operation, and maintenance of a relational database and associated applications".
This project was designed using Oracle SQL and Java.

///////////////////
/Code Organization/
//////////////////

The code is divided into 4 Java files:

* JogInterface
* CustomerOptions
* RetailStoreOptions
* SystemOptions

An explanation for each file is given below.

JogInterface

Contains the JogInterface class. The method printMainMenu is responsible for showing the main menu on the screen, which informs the user which options are available
at startup. The main method is also included in this class, from here the user will choose one of the three starting interfaces: 

* Customer Interface
	* Responsible for the following: requesting a new phone (included with a new phone number) from the online shop, changing account plan, and user authorization (required for
both of the previous options) using an account identification number and a password.
	* Requesting a phone requires the user to have an account that allows more phone numbers.

* Retail Store Manager Interface
	* This interface requires the manager to know the identification number of the store (note: this store is physical, doesn't apply to Jog's online store).
	* It is responsible for the following: checking inventory, ordering a restock from the online store, updating inventory, selling a phone to a customer, starting a new service
		for a customer and ending a service for a customer.
	* The following methods require a better explanation than others:
		* updateInventory: the manager is only allowed to update inventory using past restock orders. A list of requested restock orders will be given to the user and he will choose
			one by its identification number. After the selection, the order is removed from the restock table at the database and the inventory will be updated.
		* sellPhone: sells a phone from the store to customer, customer must have an account that is not full or that allows more phone numbers. The phone comes with a phone number randomly generated.
		* endService: closes customer account (keeps customer records), and makes all phones unactive (the date that it became unactive is stored as an attribute named network_time_period)

* System Inteface
	*This interface is responsible for reading usage records from a text file and showing the bills to be paid by the customer.
	*The text file needs to be organized in the following order:
		What is necessary for text usage
		What is necessary for call usage
		What is necessary for internet usage
		
		Note: if not inserted in this format and order or if there is any other error, the errors will be written to an error file choosen by the user (the file can be 
			created or can already exist)

///////////////////
/To be considered /
//////////////////

There are 3 types of accounts:

*Individual account: allows only one phone number.
*Family account: allows up to 4 phone numbers.
*Business account: unlimited phone numbers.

If a customer tries to order a phone online or tries to buy one at a store and the software detects that the user has a full family account or an individual account, he won't be able to acquire a new phone.
A customer is able to have more than one account.
To use the customer interface, the user must know both his account identification number and password. This is for security purposes just like it happens on other enterprises such as Verizon or Tmobile.

All 3 kinds of categories have been applied to the project:  interactive interfaces, stream-input, reporting systems (billing).

ojdbc.jar is included inside the lib file. It is necessary to run the JAR file. Don't move the file from its folder.

Special note: When compiling the Java files please note they belong to package joginterface. The JAR file was generated using Netbeans 8.1

///////////////////
/     Examples    /
//////////////////

For testing purposes, here are two sets of suggestions:

*Set 1:
	*Customer name: Vasur
	*Customer ID: 11391
	*Account ID: 10080
	*Account password: e1e8072032c
	*Account type: Family
	*Phone numbers:
			5781232311 (Primary)
			4381412400 (Other)
	*Plan type: unlimited calls and texts to other Jog customers
			

*Set 2: 
	*Customer name: Bloom
	*Customer ID: 10136
	*Account ID: 10371
	*Account password: 65f8ba558da
	*Account type: Individual
	*Phone numbers:
			9982753033 (Primary)
	*Plan type: unlimited calls and texts

	Notes: For this set, the user won't be able to buy a phone, because his account is an individual account. He must start a new service to be able to buy a new phone.
	
	For printing bills for both of these customers, I suggest choosing month 4 (April), because that is the month with the most bills to be paid by both of these customers, since that
		is the date when both the triggers to insert into bill once an insert has been made into usage has been implemented and when the testing phase for the readUsage method began.
		Note: Other months work just as fine, but April has the largest number of entries in the database due to testing.
	
For testing physical stores:

	*Store ID1 = 1316
	*Store ID2 = 1024
	
	Note: The manager must know the identification number for the store he works on. This was implemented this way since this method is used in a great deal of retail stores, because besides
		making management easier, it also makes it safer and effective.

///////////////////
/   Populating    /
//////////////////

For populating the database I used two sites to make my job easier: http://random-date-generator.com/, https://www.randomlists.com/random-addresses, and https://www.randomlists.com/phone-numbers.
The first site generates random dates for timestamps, the second generates random addresses, and the third generates random phone numbers. Both were saved into files.
Those files are inside the Populate DB Files folder. Inside that directory lies the java files, which has all the code snippets I created for populating the database, along with comments that
explicity tell the user what does each one do. The ones marked as stale are old files, which were kept for consulting purposes. These old files were used before the database design was changed one last time.