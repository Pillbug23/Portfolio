// Task 2i

db.movies_metadata.aggregate([


    //{ "title" : "The Shawshank Redemption", "vote_count" : 8358, "score" : 8.23 }
    //Step 1: Find minimum vote count
    //Step 2: Add v+m
    //Step 3: v/(v+m)
    {$project: {
        _id: 0,
        "title": 1,
        "vote_count": 1,
        "score":
        { $round : [
        { $add: [  
        //Expression 1
        { $multiply: [ "$vote_average", { $divide: [ "$vote_count", { $add: [ "$vote_count", 1838]}]}]},
        //Expression 2
        { $multiply: [ 7, { $divide: [ 1838, { $add: [ "$vote_count", 1838]}]}]}
        ] }
    , 2
    ] }
    }},

    
    //Sort in descending order of score, and break ties 
    //in descending order of vote_count and ascending order of title.
    {$sort: { score: -1,vote_count: -1, title: 1}},

    //Limit by top 20
    {$limit: 20},

]);