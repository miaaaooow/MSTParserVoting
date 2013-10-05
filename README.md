MSTParserVoting
===============

Ryan McDonald's MST Parser + Voting

The original code can be found at:
https://github.com/kzn/mstparser

The Voting component(see mstparser/VotingParser.java) is added by 
Maria Mateva, Sofia University, FMI, Bulgaria

Newly added options:

* voting-on - is the parser in voting mode; possible values:*true*, *false*; default value: *false*
* voting-mode - voting mode with possible values: *equal*, *accuracies*, *avg-accuracies*, default values: *accuracies*
* voting-parsers - which parsers' numbers to be included in the voting process; possible values: a list of numbers, merged with commas, e.g. "3,5,7"
* weighed-edges - if the values have weights, other than labels. possible values: *true*, *false*, default value: *false*




