Welcome to the Group B Financial Portfolio Tracking System Release 2 Read Me!
To run the program, run FPTS.bat.
FPTS.bat must be in the same directory as fpts.jar.
To delete a user profile, run java -jar fpts.jar -delete <userID>
All portfolios you want to load must be in a directory named portfolios that is in the same direction as fpts.jar and FPTS.bat.
If it does not exist when you create a portfolio or save a portfolio, it will be created for you, and operation (creation or saving) will continue as normal.
We require the formattedEquities.csv file containing the initial equity info to be in the same directory as fpts.jar.
We also require you to have an internet connection.


Known Bugs
	Attempting to load an older .pfo file will return an incorrect password dialog box or a wrong serialVersionUid dialog box.
	Some components do not scale when window size is changed.
	