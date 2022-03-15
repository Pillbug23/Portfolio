// Task 1iv

//Select from ratings 
db.ratings.aggregate([
    //Know their userID is 186
    {$match: { userId: 186}},

    //Sort by descending, most recent to less recent
    {$sort: { timestamp: -1}},

    //Limit by top 5
    {$limit: 5},

    //push returns an array of all values after applying an expression
    //after grouping, returns a list of movieIds,ratings,timestamps for each group
    //Note to add s
    //Hint;Group by null and push p[eratpr]
    {$group: {
        _id: null,
        movieIds: {$push: "$movieId"},
        ratings: {$push: "$rating"},
    	timestamps: {$push: "$timestamp"}
        }
    },

    { $project: {
    	_id: 0,
    	movieIds: 1,
    	ratings: 1,
    	timestamps: 1
    }}

]);