// Task 1i

db.keywords.aggregate([
    //Start by finding names with mickey mouse
    //Next find names that have marvel comic
    //elemMatch used to better find documents, no idea what it does to text
    //Or will be used to find either of our two "where" conditions
    //match will match specific documents with the properties
    {$match: {
	    $or: [
	    	{keywords: {$elemMatch: {name: "mickey mouse"}}},
	    	{keywords: {$elemMatch: {name: "marvel comic"}}}
	    ]}
    },


    //Sort by movieID, 1 denotes ascending, -1 would be descending 
    {$sort: {"movieId": 1}},

    //Will basically select columns in SQL 
    //ID explicitly project out this field
    //
    {$project: {
        _id: 0,
        movieId: 1
    }}

]);