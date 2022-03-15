  
      let total = 0; /*Total is by default 0*/
      let strbuffer = 0;
      let operator = null;
      let count = 1;
      let lastValue = null;
      
      
      /*  FUNC DESCRIPTION: Operator calculations. Create the in +, x, -, and ÷ operator calculations. The plus operator is done for you!
          Uncomment and fill in the blank spaces. */
          function calculations(number) {
            const intBuffer = parseInt(number); // Hint: Use parseInt to convert string to integer
            total = parseInt(total);
            if (operator === "+") { /*For addition*/
                total += intBuffer;
            }
            if (operator === "-") { /*For subtraction*/
              total -= intBuffer;
            }
            if (operator === "x") { /*For multiplciation*/
              total *= intBuffer;
            }
            if (operator === "÷") { /*For division*/
              total /= intBuffer;
            }
        }
        

        /*   FUNC DESCRIPTION: If user input is a number, create the function. */
        function makesNumber(value) {
            if (strbuffer === 0) {
                strbuffer = value;
            } else {
                strbuffer += value;
            }
        }
        

        /*  FUNC DESCRIPTION: If user input is not a number, create the function. Create the functionality for "C", "←", "=", and operators. */
        function makesSymbol(symbol) {
            const intBuffer = parseInt(strbuffer);
            if (symbol === 'C') {
                strbuffer = 0;
                total = strbuffer;
                document.querySelector(".result-screen").innerHTML = strbuffer;
            } else if (symbol === '←') {
               strbuffer = strbuffer.slice(0,-1);
               total = strbuffer;
               if (strbuffer.length === 0) {
                   strbuffer = 0;
                   total = 0;
               }
               document.querySelector(".result-screen").innerHTML = strbuffer;
            } else if (symbol === '=') {
                console.log(total);
                calculations(strbuffer);
                strbuffer = total;
            } else if (total === 0) {
                total = strbuffer;
                strbuffer = 0;
            } else {
                total = strbuffer;
                strbuffer = 0;
                lastValue = symbol;
            }   
            operator = symbol;
        }
      

      /*  FUNC DESCRIPTION: Write the function to set listeners. This is how we will make the HTML interactive with the JS!
          This is where we sense when a user clicks a certain button and send this information to our buttonClicked function. */
      function setListeners() {
          let allNumbers = document.querySelectorAll('.buttons');
            for (item of allNumbers) {
                item.addEventListener('click',  function () {
                // call the buttonClicked() function on its click
                buttonClicked(event.target.innerText);
                });
            }
        }
      

      //Make sure to call setListeners!!!
      setListeners();
      

      /*  FUNC DESCRIPTION: Now we will write the function that takes care of when a button is clicked. */
      function buttonClicked(valueClicked) {
          itemClicked = valueClicked.textContent;
          if (isNaN(parseInt(valueClicked))) { //NaN means "Not a Number"
            makesSymbol(valueClicked);
          } else {
            makesNumber(valueClicked);
          }
        document.querySelector(".result-screen").innerHTML = strbuffer;
      }

      