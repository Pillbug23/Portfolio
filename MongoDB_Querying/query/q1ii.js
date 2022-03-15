// Task 1ii

//Select from movies metadata
db.movies_metadata.aggregate([
    //Where condition
    //Basically find movies that are comedy
    //And have a vote count greater than 50 or equal
    {$match: {
        $and: [
	    	{genres: {$elemMatch: {name: "Comedy"}}},
            {vote_count: {$gte: 50}}
        ]},
    },

    //Sort by average vote, 1 denotes ascending, -1 would be descending 
    //Ties should be broken by vote count also descending order
    {$sort: {"vote_average": -1,"vote_count":-1,"movieId":1}},


    // Limit to only the first 50 documents
    {$limit: 50},

    //Select the id, title, average vote, and vote count 
    { $project: {
    		_id: 0,
     		title: 1,
     		vote_average: 1,
     		vote_count: 1,
     		movieId: 1
    }},

    
]);

