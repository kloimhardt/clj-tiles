(ns cljtiles.tutorials-sicm2
  (:require [cljtiles.sicm :as sc]))

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
    (fn [ifo error msg-fn]
      (let [frm (last ifo)]
        (cond
          (= frm 'time) (msg-fn '(nil time-error) nil)
          :else
          (str "An error occured. Maybe you can rearrange things so that " frm " is called before the error occurs."))))
    :message-fn
    (fn [ifo result]
      (let [frm (last ifo)
            last-ifo (cond
                       (and (coll? frm) (= (first frm) 'Path-of-a-Free-Particle))
                       ({[true true] 'Path-of-a-Free-Particle-sym-vec
                         [true false] 'Path-of-a-Free-Particle-sym
                         [false true] 'Path-of-a-Free-Particle-num-vec
                         [false false] 'Path-of-a-Free-Particle-num}
                        [(js/isNaN (js/parseInt (last frm)))
                         (= ::sc/up (first (sc/classify (first result))))])
                       (= (str frm) "'t")
                       't-symbol
                       (and (coll? frm)
                            (= '(defn Path-of-a-Free-Particle [time])
                               (take 3 frm))
                            (= 'up (first (nth frm 3 nil))))
                       'Path-of-a-Free-Particle-fn
                       :else frm)]
        (or
          (get
            {(symbol :5) "And the number 4..."
             (symbol :4) "We multiply them to give ..."
             (list '* (symbol :5) (symbol :4))
             "and add 2 resulting in ..."
             (list '+ (symbol :2) (list '* (symbol :5) (symbol :4)))
             "Now we have here a block called up. Inspecting it..."
             '(up)
             "gives an unknown type. The block does not mean anything by itself. But if we connect the formula we just created...
"
             (list 'up (list '+ (symbol :2) (list '* (symbol :5) (symbol :4))))
             "we get a column vector. If we connect the number 3, ..."
             (list 'up (list '+ (symbol :2) (list '* (symbol :5) (symbol :4))) (symbol :3))
             "we get a proper column vector in two dimensions. Now we want to make the vector time dependent. But if we inspect the variable \"time\", ..."
             'time-error
             "we get an error. This is a block which has no meaning by itself. It is meant to be a parameter of a function. So we define one and give it the name Path-of-a-free-particle, it has one argument, which is the time and returns (4 * time). Inspecting the function..."
             (list 'defn 'Path-of-a-Free-Particle ['time]
                   (list '* (symbol :4) 'time))
             "gives some cryptic output of unknown type. We need to add a block which calls the function. You open the parser, and create the call statement"
             'Path-of-a-Free-Particle-num
             "We get a number. By inspecting the parameter \"time\" of the function itself, ..."
             't-symbol
             "It is indeed a symbol"
             'Path-of-a-Free-Particle-sym
             "This is yet another new type: an Expression. It is four times t. Now you start to finish the construction of the function describing the motion of a free particle."
             'Path-of-a-Free-Particle-fn
             "You see some cyptic output. You'd better call the function."
             'Path-of-a-Free-Particle-num-vec
             "A vector of numbers. Calling the function with a symbol is certainly more interesting."
             'Path-of-a-Free-Particle-sym-vec
             "leads to the time dependent vector we more traditionally associate with the motion of a particle with constant velocity. The most important fact comes up, when we inspect the parameter time of the function:"
             }
            last-ifo)
          (when (= frm 'time)
            (let [c (map sc/classify result)]
              (if (> (count (into #{} c)) 1)
                (str "The block changes its type during the course of the program. It is first a " (last (first c)) ", than a " (last (last c))". This result, that blocks change their type, is very general. If we move to the last step of this pendulum example")
                (if (= :cljtiles.sicm/nu (first (first c)))
                  "we see that time is also a number, as we would expect from looking at the block just created. But here comes a crucial step: Not only can we call a function with a number, but we can call it with a symbol. For this, we create a new calling block with a symbol 't as the argument. You open the parser again..."
                  (str "The block \"time\" is a " (last (first c)) ". But a block can have more than one type during the course of a program.")))))
          )))

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
           'time]}])

(def chapnames ["Pendulum begin"])
(def chaps [(count e-vect)])
