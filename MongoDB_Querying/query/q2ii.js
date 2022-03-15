// Task 2ii

db.movies_metadata.aggregate([

    //First select only id and tagline
    //To split the sentence use $split
    //delimiter is space
    //Next convert each word to lower
    //Must lower first before split or error
    { $project: {
    		_id: 0,
    		splitwords: {$split: ["$tagline", " "]}
    }},
    
    //Separates each word into its own tagline word
    {$unwind: "$splitwords"},

    //First get rid of characters using trim
    //The input is split words which will be converted to lower already
    { $project: {
    	tag: {$trim: {input: {$toLower: "$splitwords"}, chars: ",.?!"}},
    	length: {$strLenCP: "$splitwords"}
    }},

    //length >3
    //Check after trimming
    {$match: {length: { $gt: 3}}},

    //Group by count for word
    //// Get the count for each group
    {$group: {_id: "$tag",count: {$sum: 1}}},

    //Sort by count greatest to least
    {$sort: { count: -1}},

    //Limit to 20
    {$limit: 20},

]);