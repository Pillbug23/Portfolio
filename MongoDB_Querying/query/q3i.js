// Task 3i

db.credits.aggregate([
    //Unwind cast to access elements
    { $unwind: "$cast" },
    
    //Match to all cast.id who have id 7624 
    //Make sure to list cast.id, not just id
    {$match: {"cast.id": {$eq: 7624}}},

    //Copy from example query in proj
    { $lookup: {
        from: "movies_metadata",
        localField: "movieId",
        foreignField: "movieId",
        as: "movies"
    }},

    
    //Project the fields, or select fields only title,release_date,character
    {$project: {
        _id:0,
        title: "$movies.title",
        release_date: "$movies.release_date",
        character: "$cast.character"
    }},

    //Unwind title and release_date since they nested
    {$unwind: "$title"},
    {$unwind: "$release_date"},

    //Order the results in descending order of release date.
    {$sort: {release_date:-1}}
]);