"""Typing test implementation"""

from utils import lower, split, remove_punctuation, lines_from_file
from ucb import main, interact, trace
from datetime import datetime


###########
# Phase 1 #
###########


def choose(paragraphs, select, k):
    """Return the Kth paragraph from PARAGRAPHS for which SELECT called on the
    paragraph returns true. If there are fewer than K such paragraphs, return
    the empty string.
    """
    # BEGIN PROBLEM 1
    revamped_list = [x for x in paragraphs if select(x) == True] # only returns list where words are of length less than 5
    if k < len(revamped_list):   #our reduced list will have less k paragraphs, and thus if condition not met we move to else
        return revamped_list[k]  #returns element k of our changed list
    else:
        return ''
    # END PROBLEM 1


def about(topic):
    """Return a select function that returns whether a paragraph contains one
    of the words in TOPIC.

    >>> about_dogs = about(['dog', 'dogs', 'pup', 'puppy'])
    >>> choose(['Cute Dog!', 'That is a cat.', 'Nice pup!'], about_dogs, 0)
    'Cute Dog!'
    >>> choose(['Cute Dog!', 'That is a cat.', 'Nice pup.'], about_dogs, 1)
    'Nice pup.'
    """
    assert all([lower(x) == x for x in topic]), 'topics should be lowercase.'
    # BEGIN PROBLEM 2
    def select(paragraph):
    #fixed problem 2, realized ins of creating 3 diff variables you can just nest them
        fixed_paragraph = split(lower(remove_punctuation(paragraph))) 
        for x in fixed_paragraph: #for all elements in our fixed function
            for y in topic: #all elements in other paragraph
                if x==y:    #if both lists contain same words return True
                    return True
        return False
    return select
    # END PROBLEM 2


def accuracy(typed, reference):
    """Return the accuracy (percentage of words typed correctly) of TYPED
    when compared to the prefix of REFERENCE that was typed.

    >>> accuracy('Cute Dog!', 'Cute Dog.')
    50.0
    >>> accuracy('A Cute Dog!', 'Cute Dog.')
    0.0
    >>> accuracy('cute Dog.', 'Cute Dog.')
    50.0
    >>> accuracy('Cute Dog. I say!', 'Cute Dog.')
    50.0
    >>> accuracy('Cute', 'Cute Dog.')
    100.0
    >>> accuracy('', 'Cute Dog.')
    0.0
    """
    typed_words = split(typed)
    reference_words = split(reference)
    # BEGIN PROBLEM 3
    count_words = 0      #Counter for counting same words
    #I just combined the two if statements for problem 3
    if len(typed_words) == 0 or len(reference_words) == 0:   #condition if typed_words is '' then return 0.0
        return 0.0
    #Problem 3 revision
    for x in range(len(typed_words)):  #for all x in range of length of typed words
        if x<=(len(reference_words)-1) and typed_words[x] == reference_words[x]:
            count_words = count_words + 1   #counter goes up if words in same spot are same
    return (count_words/len(typed_words))*100   #return words same divided by total words multiply to get perc
    
    # END PROBLEM 3


def wpm(typed, elapsed):
    """Return the words-per-minute (WPM) of the TYPED string."""
    assert elapsed > 0, 'Elapsed time must be positive'
    # BEGIN PROBLEM 4
    return len(typed)/5 * (60/elapsed) #literally just follow description, it gives the formula
    # END PROBLEM 4

def autocorrect(user_word, valid_words, diff_function, limit):
    """Returns the element of VALID_WORDS that has the smallest difference
    from USER_WORD. Instead returns USER_WORD if that difference is greater
    than LIMIT.
    """
    # BEGIN PROBLEM 5
    diff = limit + 1
    diff_word = ''
    for i in valid_words:
        if i == user_word:
            return user_word
        checker = diff_function(user_word, i, limit)
        if checker < diff:
            diff = checker
            diff_word = i
    if diff > limit:
        return user_word
    else:
        return diff_word
    # END PROBLEM 5


def shifty_shifts(start, goal, limit):
    """A diff function for autocorrect that determines how many letters
    in START need to be substituted to create GOAL, then adds the difference in
    their lengths.
    """
    # BEGIN PROBLEM 6
    #Just combined if statements to simplify code
    if limit < 0 or start==goal:   # if limit on correction is negative return 0
        return 0
    #problem 6 revisions ###########################
    if start == "" or goal =="":   # if start reduction is empty we return difference
        return abs(len(start) - len(goal))
    if start[0] == goal[0]:   #first letter vs first=same then return 2nd and process continues
        return shifty_shifts(start[1:],goal[1:],limit)
    if start[0] != goal[0]:   #if first letter doesnt make second reduce correction limit by 1 and increase correction counter by 1
        return shifty_shifts(start[1:],goal[1:],limit-1) + 1
    # END PROBLEM 6


def pawssible_patches(start, goal, limit):
    """A diff function that computes the edit distance from START to GOAL."""
    #Just combined if statements to simplify code
    if limit < 0 or start == goal: # Fill in the condition
        # BEGIN
        return 0
        # END
    #problem 7 revisions
    elif start == "" or goal =="":   # if start reduction is empty we return difference
        return abs(len(start) - len(goal))
    elif start[0] == goal[0]:   #first letter vs first=same then return 2nd and process continues
        return pawssible_patches(start[1:],goal[1:],limit)
    else:
        add_diff = pawssible_patches(goal[0] + start,goal,limit-1) + 1 #adds a letter to start, in the doctest u see its the letter from goal
        remove_diff = pawssible_patches(start[1:],goal,limit-1) + 1    #removes a letter from start, aka bump the index up by 1 every time we call
        substitute_diff = pawssible_patches(start[1:],goal[1:],limit-1) + 1  #same function from up top which we implemented alrdy
        # BEGIN
        return min(add_diff,remove_diff,substitute_diff) #whatever operations returns the least amount of edit operations
        # END


def final_diff(start, goal, limit):
    """A diff function. If you implement this function, it will be used."""
    assert False, 'Remove this line to use your final_diff function'


###########
# Phase 3 #
###########


def report_progress(typed, prompt, user_id, send):
    """Send a report of your id and progress so far to the multiplayer server."""
    # BEGIN PROBLEM 8
    correct = True
    counter = 0
    index = 0
    while correct and (counter < len(typed)):
        if typed[index] == prompt[index]:
            counter += 1
            index += 1
        else:
            correct = False
        
    progress = counter / len(prompt)
    report = {'id': user_id, 'progress': progress}
    send(report)
    return progress
    # END PROBLEM 8


def fastest_words_report(times_per_player, words):
    """Return a text description of the fastest words typed by each player."""
    game = time_per_word(times_per_player, words)
    fastest = fastest_words(game)
    report = ''
    for i in range(len(fastest)):
        words = ','.join(fastest[i])
        report += 'Player {} typed these fastest: {}\n'.format(i + 1, words)
    return report


def time_per_word(times_per_player, words):
    """Given timing data, return a game data abstraction, which contains a list
    of words and the amount of time each player took to type each word.

    Arguments:
        times_per_player: A list of lists of timestamps including the time
                          the player started typing, followed by the time
                          the player finished typing each word.
        words: a list of words, in the order they are typed.
    """
    # BEGIN PROBLEM 9
    counter = 0
    timelist = []
    times = []
    nol = len(times_per_player)
  
    if(len(times_per_player[0]) == 1):
        nothing = 0
        while nothing < nol:
            times.append([])
            nothing += 1
        return game(words, times)
        
    for i in times_per_player:
      for x in i:
        if(counter != i[len(i)-1]):
          timelist.append(x)
          counter += 1

    #print(timelist, nol)
    li = 0
    a = 0
    ok = []
    if nol == 1:
      while li < len(timelist) - 1:
        #print(li, timelist[li+1] - timelist[li])
        ok.append(timelist[li+1] - timelist[li])
        li += 1
      times.append(ok)
    else:
      while li < len(timelist) - 1:
        #print("li is: ", li)

        if li <= len(timelist)//nol - 1 + a:
          ok.append(timelist[li+1] - timelist[li])
          li += 1

        #print("li is: ", li)
        #print("current array: ", ok)

        #print("the if condition: ", li+1, " == ", len(timelist)//nol+a, " is: ", li + 1 == len(timelist)//nol + a)
        if(li + 1 == len(timelist)//nol + a):
          a = li+1
          times.append(ok)
          #print("added array to times")
          ok = []
          #print("the if condition: ", li+1, " != ", len(timelist)- 1, " is: ", li + 1 != len(timelist) - 1)
          if(li + 1 != len(timelist) - 1):
            li += 1

    #print(words)
    #print(times)
    #print(game(words, times))

    return game(words, times)
    # END PROBLEM 9


def fastest_words(game):
    """Return a list of lists of which words each player typed fastest.

    Arguments:
        game: a game data abstraction as returned by time_per_word.
    Returns:
        a list of lists containing which words each player typed fastest
    """
    player_indices = range(len(all_times(game)))  # contains an *index* for each player
    word_indices = range(len(all_words(game)))    # contains an *index* for each word
    # BEGIN PROBLEM 10
    players_update = [[] for y in player_indices] #create an empty list for each player
    for x in word_indices: #loop through every single word
        fastest_player = 0
        fastest_time_typed = time(game,fastest_player,x)
        for y in player_indices:
            time_for_player = time(game,y,x)
            if fastest_time_typed > time_for_player:
                fastest_player = y           #fastest player denoted by y
                fastest_time_typed = time_for_player
        #I just got rid of the variable here and combined the two expressions
        players_update[fastest_player].append(word_at(game,x)) #bring word player typed fastest into list
        #only revision I can think of for problem 10
    return players_update #return the lists of all players of their respective words typed fastest


    # END PROBLEM 10


def game(words, times):
    """A data abstraction containing all words typed and their times."""
    assert all([type(w) == str for w in words]), 'words should be a list of strings'
    assert all([type(t) == list for t in times]), 'times should be a list of lists'
    assert all([isinstance(i, (int, float)) for t in times for i in t]), 'times lists should contain numbers'
    assert all([len(t) == len(words) for t in times]), 'There should be one word per time.'
    return [words, times]


def word_at(game, word_index):
    """A selector function that gets the word with index word_index"""
    assert 0 <= word_index < len(game[0]), "word_index out of range of words"
    return game[0][word_index]


def all_words(game):
    """A selector function for all the words in the game"""
    return game[0]


def all_times(game):
    """A selector function for all typing times for all players"""
    return game[1]


def time(game, player_num, word_index):
    """A selector function for the time it took player_num to type the word at word_index"""
    assert word_index < len(game[0]), "word_index out of range of words"
    assert player_num < len(game[1]), "player_num out of range of players"
    return game[1][player_num][word_index]


def game_string(game):
    """A helper function that takes in a game object and returns a string representation of it"""
    return "game(%s, %s)" % (game[0], game[1])

enable_multiplayer = False  # Change to True when you're ready to race.

##########################
# Command Line Interface #
##########################


def run_typing_test(topics):
    """Measure typing speed and accuracy on the command line."""
    paragraphs = lines_from_file('data/sample_paragraphs.txt')
    select = lambda p: True
    if topics:
        select = about(topics)
    i = 0
    while True:
        reference = choose(paragraphs, select, i)
        if not reference:
            print('No more paragraphs about', topics, 'are available.')
            return
        print('Type the following paragraph and then press enter/return.')
        print('If you only type part of it, you will be scored only on that part.\n')
        print(reference)
        print()

        start = datetime.now()
        typed = input()
        if not typed:
            print('Goodbye.')
            return
        print()

        elapsed = (datetime.now() - start).total_seconds()
        print("Nice work!")
        print('Words per minute:', wpm(typed, elapsed))
        print('Accuracy:        ', accuracy(typed, reference))

        print('\nPress enter/return for the next paragraph or type q to quit.')
        if input().strip() == 'q':
            return
        i += 1


@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions."""
    import argparse
    parser = argparse.ArgumentParser(description="Typing Test")
    parser.add_argument('topic', help="Topic word", nargs='*')
    parser.add_argument('-t', help="Run typing test", action='store_true')

    args = parser.parse_args()
    if args.t:
        run_typing_test(args.topic)