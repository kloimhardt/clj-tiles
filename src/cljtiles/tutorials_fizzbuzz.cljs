(ns cljtiles.tutorials-fizzbuzz
  (:require
   [cljtiles.tutorials-advent1 :as adv]
   [cljtiles.codeexplode :as explode]))

(defn explode-all [tuts-mapvec]
  (map #(-> %
            ;;(assoc :solpos-yx (adv/calc-y-for-solution (:solution %)))
            (merge (explode/explode (:code %) nil)))
       tuts-mapvec))

(def e-vect
  [{:description
    [:div
     [:p "This program tackles a classic job interview problem using Clojure."]
     [:p "The requirements for a fizz-buzz program are to print a series of integer values with the exception that all numbers evenly divisible by 3 should be replaced with the word \" fizz \". All numbers evenly divisible by 5 should be replaced with the word \" buzz \". All words evenly divisible by 3 and 5 should be replaced with the word \" fizz-buzz \"."]
     [:p "I use functions, local bindings, overloads, and iteration techniques in this program."]
     [:p "I'll start by making a fizz-buzz function."]
     [:p "This first version takes no parameters."]]
    :solution ['(defn fizz-buzz [])]
    :code ['(defn fizz-buzz [])]}
   {:description [:div
                  [:p "I will need a starting and ending value from the user to determine the range of values to print."]
                  [:p "I can store these in the local bindings of a let"]
                  [:p "Remember, these are sort of like local variables."]]
    :solution ['(defn fizz-buzz []
                  (:tiles/vert (let (:tiles/vert [start 1
                                                  end 4]))))]
    :code ['(:tiles/keep (defn fizz-buzz [] :tiles/slot))
           '(let [start 1
                  end 4])]}
   {:description
    [:div
     [:p "I print out the values to make sure it all worked."]
     [:p "All local bindings have a scope until the ending parenthesis of the call to let."]]
    :solpos-yx [[0 0] [300 0]]
    :solution ['(defn fizz-buzz []
                  (:tiles/vert
                   (let (:tiles/vert [start 1 end 4])
                     (println "Start: " start)
                     (println "End: " end))))
               '(fizz-buzz nil)]
    :blockpos-yx [[0 0] [200 0] [300 0]]
    :code ['(:tiles/keep (defn fizz-buzz []
                           (:tiles/vert
                            (let [start 1 end 4]
                              :tiles/slot :tiles/slot))))
           '(println "Start: " start)
           '(println "End: " end)
           '(fizz-buzz nil)]}
   {:description
    [:div
     [:p "Next, I'll create another local binding to hold a sequence of numbers from start to end."]
     [:p "range is a function that returns a sequence of values between two points."]
     [:p "The first parameter is where the range of values will start. "]
     [:p "The last parameter specifies where the range will end. The ending value is exclusive meaning that the range stops before the end value. If I want to include the end value (making it inclusive) I have to add one to it."]]
    :solpos-yx [[0 0] [400 0]]
    :solution
    ['(defn fizz-buzz []
        (:tiles/vert
         (let (:tiles/vert [start 1 end 4
                            nums-in-range (range start (+ end 1))])
           (println "Start: " start)
           (println "End: " end))))
     '(fizz-buzz nil)]
    :code
    ['(:tiles/keep
       (defn fizz-buzz []
         (:tiles/vert
          (let (:tiles/vert
                [start 1
                 end 4
                 :tiles/slot :tiles/slot])
            (println "Start: " start)
            (println "End: " end)))))
     ''nums-in-range '(range start (+ end 1))
     '(:tiles/keep
       (fizz-buzz nil))]}
   {:description
    [:div
     [:p "Here I print out the values in nums-in-range to make sure I am getting all of them from the start to the end. The attached screenshot shows the output."]]
    :solpos-yx [[0 0] [300 0]]
    :solution
    ['(defn fizz-buzz []
        (:tiles/vert
         (let (:tiles/vert [start 1 end 4
                            nums-in-range (range start (+ end 1))])
           (println "nums in range" nums-in-range))))
     '(fizz-buzz nil)]
    :code
    ['(:tiles/keep
       (defn fizz-buzz []
         (:tiles/vert
          (let* (:tiles/vert
                 [start 1
                  end 4
                  nums-in-range (range start (+ end 1))])
                (_println "_Start: " _start)
                (_println "_End: " _end)))))
     '(let :tiles/slot (println "nums in range" nums-in-range))
     '(:tiles/keep
       (fizz-buzz nil))]}
   {:description
    [:div
     [:p "Next, I want to get a sequence of values to print to the screen for each number in the user specified range."]
     [:p "So, I will use nums-in-range and the map function to send each numeric value to a function called fizz-or-buzz-or-number."]
     [:p "Remember, the map function works by iterating over the values of a sequence and passes them into a one parameter function."]
     [:p "The function will take a single parameter and determine what should be printed for that number. "]
     [:p "It will return \" fizz-buzz \", \" fizz \", \" buzz \", or the number."]
     [:p "These values are collected in a new sequence and stored in the local binding final-output."]
     [:p "I will eventually print all of these return values."]
     [:p ""]]
    :solpos-yx [[0 0] [300 0]]
    :solution ['(defn fizz-buzz []
                  (:tiles/vert
                   (let [start 1 end 4]
                     (:tiles/vert
                      (let (:tiles/vert
                            [nums-in-range (range start (+ end 1))
                             final-output (map fizz-or-buzz-or-number nums-in-range)])
                        (println "nums in range" nums-in-range))))))
               '(fizz-buzz nil)]
    :blockpos-yx [[0 0]]
    :code []}

   {:description
    [:div
     [:p "This is the function that will determine what gets printed out."]
     [:p "It takes a number and returns one of the acceptable values."]]
    :solpos-yx [[0 0] [200 0] [500 0]]
    :solution ['(defn fizz-or-buzz-or-number [number])
               '(defn fizz-buzz []
                  (:tiles/vert
                   (let [start 1 end 4]
                     (:tiles/vert
                      (let (:tiles/vert
                            [nums-in-range (range start (+ end 1))
                             final-output (map fizz-or-buzz-or-number nums-in-range)])
                        (println "nums in range" nums-in-range))))))
               '(fizz-buzz nil)]
    :blockpos-yx [[0 0]]
    :code []}

   {:description
    [:div
     [:p "The first thing I need to know is if the passed in number is divisible by 3."]
     [:p "I can create a local binding to a boolean to store this."]]
    :solpos-yx [[0 0] [200 0] [500 0]]
    :solution ['(defn fizz-or-buzz-or-number [number]
                  (let [divisible-by-3]))
               '(defn fizz-buzz []
                  (:tiles/vert
                   (let [start 1 end 4]
                     (:tiles/vert
                      (let (:tiles/vert
                            [nums-in-range (range start (+ end 1))
                             final-output (map fizz-or-buzz-or-number nums-in-range)])
                        (println "nums in range" nums-in-range))))))
               '(fizz-buzz nil)]
    :blockpos-yx [[0 0]]
    :code []}

   {:description
    [:div
     [:p "The mod function performs a modulus division and returns a remainder."]
     [:p "In some other programming languages this would look like this:
"]
     [:p "number % 3"]
     [:p ""]]
    :solpos-yx [[0 0] [200 0] [500 0]]
    :solution ['(defn fizz-or-buzz-or-number [number]
                  (let [divisible-by-3 (mod number 3)]))
               '(defn fizz-buzz []
                  (:tiles/vert
                   (let [start 1 end 4]
                     (:tiles/vert
                      (let (:tiles/vert
                            [nums-in-range (range start (+ end 1))
                             final-output (map fizz-or-buzz-or-number nums-in-range)])
                        (println "nums in range" nums-in-range))))))
               '(fizz-buzz nil)]
    :blockpos-yx [[0 0]]
    :code []}

   {:description
    [:div
     [:p "I want to know if the remainder is equal to 0. If it is, this means that the number is divisible by 3."]
     [:p "The call to = returns either true or false which is stored in the binding divisible-by-3."]]
    :solpos-yx [[0 0] [200 0] [500 0]]
    :solution ['(defn fizz-or-buzz-or-number [number]
                  (let [divisible-by-3 (= (mod number 3) 0)]))
               '(defn fizz-buzz []
                  (:tiles/vert
                   (let [start 1 end 4]
                     (:tiles/vert
                      (let (:tiles/vert
                            [nums-in-range (range start (+ end 1))
                             final-output (map fizz-or-buzz-or-number nums-in-range)])
                        (println "nums in range" nums-in-range))))))
               '(fizz-buzz nil)]
    :blockpos-yx [[0 0]]
    :code []}

   {:description
    [:div
     [:p "I write some similar code to find out if the number is divisible by 5. "]
     [:p ""]
     [:p ""]
     [:p ""]]
    :solpos-yx [[0 0]]
    :solution []
    :blockpos-yx [[0 0]]
    :code []}

   {:description
    [:div
     [:p ""]
     [:p ""]
     [:p ""]
     [:p ""]]
    :solpos-yx [[0 0]]
    :solution []
    :blockpos-yx [[0 0]]
    :code []}])

(def chapnames ["Fizzbuzz"])
(def chaps [(count e-vect)])

(def content {:tutorials (explode-all e-vect) :chapnames chapnames
              :chaps chaps})
