// Task 3ii

db.credits.aggregate([
    // Wes Anderson's id in the credits collection is 5655. To get started, 
    //try to match all documents in credits where he is listed as the director.

    //Unwind crew to access elements
    {$unwind: "$crew"},

    //Filter by director having id 5655
    { $match: {$and: [{ "crew.id": 5655},{"crew.job": "Director"}]}},

    //Unwind crew to access elements
    {$unwind: "$cast"},

    //Match cast names and id  with anderson listed as title director
    {$project: {
        _id: "$movieId",
        castID: "$cast.id",
        castName: "$cast.name"
    }},

    //Gorup by cast name. and id to get count
    //You may need to group by multiple fields for this question. 
    //For example, {$group: _id: {val1: "$field.val1", val2: "$field.val2"}}
    {$group: {
        _id: {groupedId: "$castID",groupedName: "$castName"},
        count: {$sum: 1}
    }},

    //Project the fields, or select fields only title,release_date,character
    {$project: {
        _id:0,
        count: 1,
        id: "$_id.groupedId",
        name: "$_id.groupedName"
    }},

    //Order in descending order of the number of collaborations. 
    //Break ties in ascending order of the actor's id. 
    {$sort: {count: -1, id: 1}},

    //Limit to 5 actors
    {$limit: 5}


    


    
]);