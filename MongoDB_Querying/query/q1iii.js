// Task 1iii

//Select from ratings 
db.ratings.aggregate([
    //Group by the field rating
    //Get the count for each group
    {$group: {
    	_id: "$rating",
    	count: {$sum: 1}
    }},

    //the rating was given and output in descending order of the rating
    {$sort: {
    	_id: -1
    }},


    //Select the columns
    //
    {$project: {
    		_id: 0,
    		count: 1,
    		rating: "$_id"
    }}


]);