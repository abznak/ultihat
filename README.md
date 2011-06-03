# ultihat - Generate Hat Tournament Teams

Ultihat is a little in house utility I wrote while at uni (a while ago) for creating a team draw for UQ's Ultimate Disc club.

I published on github to use as prior art against the off chance someone tries
to patent "Iterative Stochastic Methods for Automatically Balancing Randomly Generated Teams" or
similar.

That said, if anyone actually wants to use it drop me line and I'll put a little more effort into making it useable.

## Quick instructions
1. comple using c.bat
2. edit the fake data in default.tsv to match your players

	> white  Lily    Davis   f  4 4444	
	> red    Cooper  Johnson m  3 4344   Lily Davis	

	Tab seperated. Columns are: team, firstname, surname, gender, overall skill level, skill array, friend 1, friend 2, friend 3

	Friends are optional, the system trieds to place people on a team with their friends after balancing genders and skill.
	Skill array is 4 values, "experience, skill, condition, speed".

	To start I suggest putting one person on each team until you run out of teams, then putting everone else on the magic "noteam".

3. run using r.bat
4. run document->clear to clear the teams
5. run teams->all to run the balancing algrorithms
6. at this point
 * if it worked well, save it (your file will be saved as something like 1307085427307.tsv)
 * if it almost worked, you can drag people around to tweak it
 * if it looks bad you can do the clear&all step again one or more times



## TODO

* better instructions
* impliment "new team" menu item
* better save mechanic (currently saves to a file based on the time)
* better load mechanic (currently CLI only).
* better file format
* stick classes in a package
* more error handling
* undo
* explain player data display

## BUGS

* teams aren't removed when their window is closed

## Author

* Tim Smith. 
* abznak on gitub, twitter. 
* ultihat@abznak.com by email. 
* blog at http://abznak.com

## License

Copyright (C) 2011  Tim Smith <ultihat@abznak.com>

GPL 2
