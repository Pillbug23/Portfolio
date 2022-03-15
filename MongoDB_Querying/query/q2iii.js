// Task 2iii

db.movies_metadata.aggregate([
    //ne Compares two values and returns true if equal false if not
    //Hint: You can check if a field is present in a document by checking whether the field is equal to undefined
    {
    	$project: {
            //if
    		budgetagg: {$cond: [{$and: [{$ne: ["$budget", null]},{$ne: ["$budget", false]},{$ne: ["$budget", ""]},{$ne: ["$budget", undefined]}]},

                     //then
                     //,{$trim: {input: "$budget", chars: "$ "}}
    				 {$round: [{$cond: [{$isNumber: "$budget"}, "$budget", {$toInt: {$trim: {input: "$budget", chars: " USD$"}}} ]}, -7]}, 
                     
                     //else
                     "unknown"]}
    	} 
    },

    { $group: {
    	_id: "$budgetagg",
    	count: {$sum: 1}
    }},

    { $project: {
    	_id: 0,
        budget:  "$_id",
    	count: 1
    }},

    { $sort: {
        budget: 1
    }},


]);