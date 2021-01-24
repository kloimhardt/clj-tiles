(ns cljtiles.tutorials-sicm2
  (:require [cljtiles.code-analysis :as ca]))

(def bold {:style {:font-weight "bold"}})

(def e-vect
  [{:description
    [:div
     [:div bold "Description"]
     [:p "We start by creating a function Path-of-a-Free-Particle. Newtons first law states that in some inertial frame of reference, an object continues to move in space at a constant velocity. This movement takes time, so our function depends on time. It returns a vector of two elements because we choose our path to live in two dimensions."]

     [:div bold "Explanation"]
     [:p "
In familiar notation, the path is denoted by:
   \\[\\vec{x}(t) =
      \\begin{pmatrix}
      x^1(t) \\\\
      x^2(t)
      \\end{pmatrix}
=
      \\begin{pmatrix}
      x(t) \\\\
      y(t)
      \\end{pmatrix}
=
      \\begin{pmatrix}
      2 + 5t \\\\
      3 + 4t
      \\end{pmatrix}

      \\]"]
     [:p "Note that \\(\\vec{x}\\) is a column vector with according superscripted component-indizes, hence the name \"up\" in the code."]
     [:p "The vector \\(\\vec{x}\\) describes a moving body which is at time \\(t=0\\) at point
\\(\\big(\\begin{smallmatrix}
  x\\\\
  y
\\end{smallmatrix}\\big)\\)
=
\\(\\big(\\begin{smallmatrix}
  2\\\\
  3
\\end{smallmatrix}\\big)\\)
and has a constant speed of \\(5 \\frac{m}{s}\\) in \\(x\\) direction and \\(4 \\frac{m}{s}\\) in \\(y\\) direction. Imagine the body as a "
      [:a {:href "https://www.youtube.com/watch?v=z74OwRy8o9I"} "Pizza in space"]
      " (and always think of a sattelite when someone talks about \"inertial frame of reference\")"]]
    :hint ["(Path-of-a-Free-Particle :tiles/slot) 10"
           "(Path-of-a-Free-Particle :tiles/slot) 't"]
    :error-message-fn
    (fn [ifo code error msg-fn]
      (println "err-msg " ifo)
      (def io ifo)
      (let [frm (last ifo)]
        (cond
          (= frm '(up)) (msg-fn '(nil (up-error)) code)
          (= frm 'time) (msg-fn '(nil time-error) code)
          :else
          (str "Maybe you can rearrange things so that " frm " is called before the error occurs."))))
    :message-fn
    (fn [ifo code]
      (println "msg " ifo)
      (def i ifo)
      (let [frm (last ifo)
            last-ifo (cond
                       (and (coll? frm) (= (first frm) 'Path-of-a-Free-Particle))
                       (if (js/isNaN (js/parseInt (last frm)))
                         'Path-of-a-Free-Particle-sym
                         'Path-of-a-Free-Particle-num)
                       (= (str frm) "'t")
                       't-symbol
                       :else frm)]
        (get
          {(symbol :5) "And the number 4..."
           (symbol :4) "We multiply them to give ..."
           (list '* (symbol :5) (symbol :4))
           "and add 2 resulting in ..."
           (list '+ (symbol :2) (list '* (symbol :5) (symbol :4)))
           "Now we have here a block called up. Inspecting it..."
           '(up-error)
           "gives an error. The block does not mean anything by itself. But if we connect the formula we just created...
"
           (list 'up (list '+ (symbol :2) (list '* (symbol :5) (symbol :4))))
           "we get a column vector. If we connect the number 3, ..."
           (list 'up (list '+ (symbol :2) (list '* (symbol :5) (symbol :4))) (symbol :3))
           "we get a proper column vector in two dimensions. Now we want to make the vector time dependent. But if we inspect the variable \"time\", ..."
           'time-error
           "we again get an error. This is another block which has no meaning by itself. It is meant to be a parameter of a function. So we define one and give it the name Path-of-a-free-particle, it has one argument, which is the time and returns the (4 * time). Inspecting the function..."
           (list 'defn 'Path-of-a-Free-Particle ['time]
                 (list '* (symbol :4) 'time))
           "gives some cryptic output of unknown type. We need to add a block which calls the function. You open the parser, and create the call statement"
           'Path-of-a-Free-Particle-num
           "We get a number. By inspecting the parameter \"time\" of the function itself, ..."
           'time
           "we see that time is also a number, as we would expect from looking at the block just created. But here comes a crucial step: Not only can we call a function with a number, but we can call it with a symbol. For this, we create a new calling block with a symbol 't as the argument. You open the parser again..."
           't-symbol
           "It is indeed a symbol"
           'Path-of-a-Free-Particle-sym
           "This is yet another new type: an Expression. It is four times t. Now you start to finish the construction of the function describing the motion of a free particle.
"

           }
          last-ifo)))

    :scroll [0 0]
    :blockpos [[0 0] [100 0] [250 0]
               [400 0] [500 0]
               [0 50]
               [300 100]
               [350 110]
               [0 150] [150 150]
               [0 250] [150 250]
               [0 300] [150 300]]
    :code [5
           '(* :tiles/slot :tiles/slot)
           4
           2
           '(+ :tiles/slot :tiles/slot)
           '(:tiles/vert (up :tiles/slot :tiles/slot))
           3
           'time
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           'Path-of-a-Free-Particle
           '(* :tiles/slot :tiles/slot)
           'time
           '(+ :tiles/slot :tiles/slot)
           'time]}
   {:description
    "You run the program, but the output does not convey much yet."
    :scroll [0 0]
    :blockpos [[0 0]]
    :code
    '(defn Path-of-a-Free-Particle
       time
       (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))}])

(def chapnames ["SICM2"])
(def chaps [(count e-vect)])
