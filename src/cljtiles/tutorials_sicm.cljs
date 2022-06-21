(ns cljtiles.tutorials-sicm)

(def bold {:style {:font-weight "bold"}})

(def e-vect
  [{:description
    [:div
     [:div bold "Introduction"]
     [:p "This chapter is a step by step guide to derive the equation of motion for the driven pendulum. An animation can be seen at the " [:a {:href "http://blog.littleredcomputer.net/math/js/2017/02/09/driven-pendulum.html"} "blog of Colin Smith"] " who also gave a "
      [:a {:href "https://www.youtube.com/watch?v=7PoajCqNKpg&t=2145s"} "Clojure/West 2017 talk"] ". Also there is an explanation " [:a {:href "https://www.youtube.com/watch?v=V7unwER5wFc"} "video"]  "."]
     [:div bold "Description"]
     [:p "We start by creating a function Path-of-a-Free-Particle. Newtons first law states that in some inertial frame of reference, an object continues to move in space at a constant velocity. This movement takes time, so our function depends on time. It returns a vector of two elements because we choose our path to live in two dimensions."]

     [:div bold "Explanation"]
     [:p "
Modelling the path of a free particle is the first step for creating the equatios of motion for the driven pendulum in a gravitational field.
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
    :blockpos-yx [[0 0] [20 300] [70 100]
                  [70 200] [70 450]
                  [150 200] [150 450]]
    :code ['(defn Path-of-a-Free-Particle
              :tiles/slot
              :tiles/slot)
           'time
           '(:tiles/vert
             (up :tiles/slot
                 :tiles/slot))
           '(:tiles/infix (+ :tiles/slot (:tiles/infix (* 5 time))))
           '2
           '(:tiles/infix (+ 3 (:tiles/infix (* :tiles/slot time))))
           '4]
    :solution ['(defn Path-of-a-Free-Particle
                  time
                  (:tiles/vert
                   (up
                    (:tiles/infix (+ 2 (:tiles/infix (* 5 time))))
                    (:tiles/infix (+ 3 (:tiles/infix (* 4 time)))))))]}
   {:description
    [:div
     [:div bold "Description"]
     [:p "The kinetic energy is half the mass times velocity squared. If we set the mass to \\(2kg\\) the formula becomes simpler. We know that the velocity of our free particle is constant. In the \\(x\\) direction it is \\(5 \\frac{m}{s}\\). But notice that the velocity here could be any function in time (not in space+time.)"]
     [:div bold "Explanation"]
     [:p "You define the kinetic energy as another function, which is not yet connected to the function defined before. The kinetic energy is a function of velocity, \\(T=T(\\vec{v}) = \\frac{m}{2} |\\vec{v}|^2 = \\frac{m}{2}v^2\\). With \\(m=2\\) you get \\( T=v^2\\) and with \\(v=\\sqrt{5^2 + 4^2}\\frac{m}{s}\\), you get \\(T=41 \\frac{kg\\ m^2}{s^2}\\). Again, \\(v\\) is a constant here, as we are dealing with the free particle. But \\(v\\) will become a function of time \\(v=v(t)\\). That means you need to accept the fact that \\(T\\) will become a function of a function. And, to make no mistake, velocity never is a function of position but only of time."]]
    :scroll [0 0]
    :blockpos [[0 0] [0 170] [0 260] [0 300] [0 340] [150 340]]
    :code ['(defn Path-of-a-Free-Particle
              time
              (:tiles/vert
               (up (:tiles/infix (+ 2 (:tiles/infix (* 5 time))))
                   (:tiles/infix (+ 3 (:tiles/infix (* 4 time)))))))
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           'Kinetic-Energy
           'velocity
           '(square :tiles/slot)
           'velocity]
    :solpos-yx [[0 0] [170 0]]
    :solution ['(:tiles/keep
                 (defn Path-of-a-Free-Particle
                   time
                   (:tiles/vert
                    (up (:tiles/infix (+ 2 (:tiles/infix (* 5 time))))
                        (:tiles/infix (+ 3 (:tiles/infix (* 4 time))))))))
               '(defn Kinetic-Energy
                  velocity
                  (square velocity))]}
   {:description
    [:div
     [:div bold "Description"]
     [:p "Now for the Lagrangian function. For our free particle, the Lagrangian is just the kinetic energy. In general, a Lagrangian does not only depend on the velocity, but also on time and position. We are not using time and position here, but they nevertheless have to appear in the input vector."]
     [:div bold "Explanation"]
     [:p "The term \"input vector\" is maybe misleading, it should better be called \"arguments of the function\" or \"function parameters\"."]
     [:p "The Lagrangian always is a function of three parameters: time, position and velocity, \\( L=L(t, \\vec{x}, \\vec{v}) \\), so the Lagrangian is a function of a number and in general two functions. At the moment, \\(L\\) only depends on the velocity, \\(L=L(\\vec{v})\\)."]
     [:p "You come to the conclusion: The Lagrangian always is a function of the parameter time which at the moment does not depend on time at all. This sounds paradoxical only due to the limits of usual mathematical notation and natural language usage. The conclusion is completely right. Time appears in the arguments of the function, but not in its body."]]
    :scroll [0 0]
    :blockpos [[0 0] [0 170] [0 280] [150 280] [150 330] [350 330]
               [300 250] [400 250] [550 250] [650 250] [750 250]]
    :code ['(defn Path-of-a-Free-Particle
              time
              (:tiles/vert
               (up (:tiles/infix (+ 2 (:tiles/infix (* 5 time))))
                   (:tiles/infix (+ 3 (:tiles/infix (* 4 time)))))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           'Lagrangian
           '(Kinetic-Energy :tiles/slot)
           'velocity
           [:tiles/slot]
           [:tiles/slot :tiles/slot :tiles/slot]
           'time
           'position
           'velocity]
    :solpos-yx [[0 0] [170 0] [280 0]]
    :solution ['(:tiles/keep
                 (defn Path-of-a-Free-Particle
                   time
                   (:tiles/vert
                    (up (:tiles/infix (+ 2 (:tiles/infix (* 5 time))))
                        (:tiles/infix (+ 3 (:tiles/infix (* 4 time))))))))
               '(:tiles/keep
                 (defn Kinetic-Energy
                   velocity
                   (square velocity)))
               '(defn Lagrangian
                  [[time position velocity]]
                  (Kinetic-Energy velocity))]}
   {:description
    [:div
     [:div bold "Description"]
     [:p "The lagrangian equations are differential equations. They are constructed out of the Lagrangian function. The differential equations are applied to some path function. Here we use our specific Path-of-a-Free-Particle. The result is in general a function of time. We set the time to ten seconds here. If the path we fed into the equtions is a viable physical path, the result shoud be a very special function: it should be the zero vector."]
     [:div bold "Explanation"]
     [:p "What is called here the Lagrangian equations can be illustrated with the following mathematical expression:
\\[
m
\\frac{d^2 \\vec{x}(t)}{dt^2}
\\]
It represents Newton's equation without the \\(=0\\) part. The name Lagrangian equations is thus maybe misleading, Lagrangian differential operator would be more exact. Or more accurate: a two dimensional row vector of differential operators:
\\[
\\left[ m \\frac{d^2 }{dt^2} \\ \\ m \\frac{d^2 }{dt^2} \\right]
\\]
This operator is produced by the"
      [:img {:src "LagrangianBlock.png" :alt "(Lagrange-equations Lagrangin)"}]
      " block.
If you now apply this operator to the Path-of-a-Free-Particle, the zero row vector is obtained becuase Path-of-a-Free-Particle is only linear in time and thus its second derivative is zero. And the above program shows exactly this."]
     [:p "Even if you do not understand the insanely complex last block, you still have the consolation that it works. Run the workspace and you indeed see the zero vector."]
     [:p
      "The mathematical expressions here can only be some kind of guide. To understand how it all fits together, you have to play with the blocks. A full understanding is not needed for playing and investigating. However, the theoretical underpinnings can be found in " [:a {:href "ftp://publications.ai.mit.edu/ai-publications/2002/AIM-2002-018.ps"} "this paper"]
      "."]]
    :scroll [0 -120]
    :blockpos [[0 0] [0 170] [0 280]
               [0 420]
               [0 500] [150 500] [300 500]
               [400 450] [450 400] [600 350]]
    :code ['(defn Path-of-a-Free-Particle
              time
              (:tiles/vert
               (up (:tiles/infix (+ 2 (:tiles/infix (* 5 time))))
                   (:tiles/infix (+ 3 (:tiles/infix (* 4 time)))))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           '[:div>tex :tiles/slot]
           '(:tiles/slot :tiles/slot)
           '(:tiles/slot :tiles/slot)
           '(Lagrange-equations :tiles/slot)
           'Lagrangian
           'Path-of-a-Free-Particle
           '10]
    :solpos-yx [[0 0] [170 0] [280 0]
                [420 0]]
    :solution ['(:tiles/keep
                 (defn Path-of-a-Free-Particle
                   time
                   (:tiles/vert
                    (up (:tiles/infix (+ 2 (:tiles/infix (* 5 time))))
                        (:tiles/infix (+ 3 (:tiles/infix (* 4 time))))))))
               '(:tiles/keep (defn Kinetic-Energy
                               velocity
                               (square velocity)))
               '(:tiles/keep (defn Lagrangian
                               [[time position velocity]]
                               (Kinetic-Energy velocity)))
               '[:div>tex
                 (((Lagrange-equations Lagrangian)
                   Path-of-a-Free-Particle)
                  10)]]}
   {:description
    [:div
     [:div bold "Description"]
     [:p
      "You replace the Path-of-a-Free-Particle. You create a vector of two arbitrary funtions. Call them q_x and q_y. Then do the replacement of the specific path with the arbitrary path. After running the workspace again, you see the general equations of motion for the free particle."]
     [:div bold "Explanation"]
     [:p
      " You make the replacement
\\[
     \\begin{pmatrix}
     2 + 5t \\\\
     3 + 4t
     \\end{pmatrix}
\\rightarrow
     \\begin{pmatrix}
     q_x(t) \\\\
     q_y(t)
     \\end{pmatrix}
     \\]
The result of the program cannot be the zero vector anymore, as the new function
 \\(\\big(\\begin{smallmatrix}
  q_x\\\\
  q_y
\\end{smallmatrix}\\big)\\)
my not be linear in time."]
     [:p "The actual result in usual mathematical notation is:
\\[
\\left[ 2 \\frac{d^2 q_x(t)}{dt^2}\\Bigg|_{t=10}\\ \\ 2 \\frac{d^2 q_y(t)}{dt^2}\\Bigg|_{t=10}\\right]
\\]
"]
     [:p "Run the workspace to see the general equations of motion for the free particle."]
     [:p "The result you see upon running the correctly arranged blocks looks different. It is an arguably simpler notation. The rational for this notation is again given in the paper linked before."]]
    :scroll [0 -120]
    :blockpos [[0 170] [0 280] [0 420]
               [0 500]
               [0 550] [200 550] [350 550] [550 550]]
    :code ['(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           '[:div>tex
             (((Lagrange-equations Lagrangian)
               Path-of-a-Free-Particle)
              10)]
           '(up :tiles/slot :tiles/slot)
           '(literal-function :tiles/slot)
           ''q_x
           '(literal-function :tiles/slot)
           ''q_y]
    :solpos-yx [[170 0] [280 0] [420 0]]
    :solution ['(:tiles/keep (defn Kinetic-Energy
                               velocity
                               (square velocity)))
               '(:tiles/keep (defn Lagrangian
                               [[time position velocity]]
                               (Kinetic-Energy velocity)))
               '[:div>tex
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  10)]]}
   {:description
    [:div
     [:div bold "Description"]
     [:p
      "Now, you want to make the equations prettier. First, you replace 10 by some time \\(t\\),
second you introduce the mass \\(m\\) into the kinetic energy. After running, you see Newtons equations of motion for the two dimensional free particle in their standard form."]
     [:div bold "Explanation"]
     [:p "The result now is
\\[
\\left[ m \\frac{d^2 q_x(\\tau)}{d\\tau^2}\\Bigg|_{\\tau=t}\\ \\ m \\frac{d^2 q_y(\\tau)}{d\\tau^2}\\Bigg|_{\\tau=t}\\right]
\\]
"]]
    :scroll [0 0]
    :blockpos [[0 0]
               [350 0] [400 0] [550 0]
               [350 50]
               [0 130]
               [900 210]
               [0 290]]
    :code ['(defn Kinetic-Energy
              velocity
              (square velocity))
           ''m
           '(:tiles/infix (/ :tiles/slot :tiles/slot))
           '2
           '(:tiles/infix (* :tiles/slot :tiles/slot))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           ''t
           '[:div>tex
             (((Lagrange-equations Lagrangian)
               (up (literal-function 'q_x)
                   (literal-function 'q_y)))
              10)]]
    :solpos-yx [[0 0] [150 0] [300 0]]
    :solution ['(defn Kinetic-Energy
                  velocity
                  (:tiles/infix (* (:tiles/infix (/ 'm 2))
                                   (square velocity))))
               '(:tiles/keep (defn Lagrangian
                               [[time position velocity]]
                               (Kinetic-Energy velocity)))
               '[:div>tex
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  't)]]}
   {:description
    [:div
     [:p "Now you model a particle in the gravitational field by introducing the potential energy. It depends on
the hight of the particle above ground. It is \\(m \\times g \\times hight\\), where \\(g\\) is \\(9.81 \\frac{m}{s^2}\\), the acceleration due to the gravity of the earth. The Lagrangain is kintic energy minus potential energy. And the Lagrangian now depends on the hight."]
     [:p "Run the workspace and Newtons equations of motion for the particle in the homogeneous gravitational field are delivered."]]
    :scroll [0 0]
    :blockpos [[0 0] [0 150]
               [100 120] [100 170]
               [100 220] [180 220] [300 220] [380 220] [500 220]
               [0 300]
               [450 300] [550 300] [600 300]
               [300 400] [450 400] [650 400]
               [0 500]]
    :code ['(defn Kinetic-Energy
              velocity
              (:tiles/infix (* (:tiles/infix (/ 'm 2)) (square velocity))))
           '(defn :tiles/slot
              :tiles/slot
              :tiles/slot)
           'Potential-Energy
           'hight
           ''m
           '(:tiles/infix (* :tiles/slot :tiles/slot))
           ''g
           '(:tiles/infix (* :tiles/slot :tiles/slot))
           'hight

           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           [:tiles/slot :tiles/slot]
           '_
           'hight
           '(:tiles/infix (- :tiles/slot :tiles/slot))
           '(Potential-Energy :tiles/slot)
           'hight

           '[:div>tex
             (((Lagrange-equations Lagrangian)
               (up (literal-function 'q_x)
                   (literal-function 'q_y)))
              't)]]
    :solpos-yx [[0 0]
                [150 0]
                [300 0]
                [500 0]]
    :solution ['(:tiles/keep (defn Kinetic-Energy
                               velocity
                               (:tiles/infix (* (:tiles/infix (/ 'm 2))
                                                (square velocity)))))
               '(defn Potential-Energy
                  hight
                  (:tiles/infix (* (:tiles/infix (* 'm 'g)) hight)))
               '(defn Lagrangian
                  [[time [_ hight] velocity]]
                  (:tiles/infix (- (Kinetic-Energy velocity)
                                   (Potential-Energy hight))))
               '(:tiles/keep [:div>tex
                              (((Lagrange-equations Lagrangian)
                                (up (literal-function 'q_x)
                                    (literal-function 'q_y)))
                               't)])]}
   {:description
    [:div
     [:p "To get the equations for the pendulum, you introduce a transformation of coordiantes. The new single coordinate is the angle between the rod and its position at rest. So the old coordinates become dependent on each other. The horizontal position x of the bob is \\( l \\times \\sin(angle)\\), where l is the length of the pendulum. The hight of the bob is \\(h - l \\times \\cos(angle)\\) (h is the hight of the pivot). The Lagangian of the pendulum is obtained by prepending the transformation. In this function composition, the Lagrangian of the independently moving particle is reused completely unchanged. The new Lagrangian equations are now applied to some function \\(\\phi\\). You can do the functional replacements using the blcks you just built."]
     [:p "Run the workbook and get the equations for the pendulum delivered."]]
    :scroll [0 -400]
    :blockpos [[0 0]
               [0 150]
               [0 300]
               [100 450]
               [0 470] [100 500] [200 500] [300 500] [400 500] [500 500]
               [100 570] [200 570] [300 570] [400 570] [500 570]
               [200 630] [300 630] [400 630] [500 630] [600 630] [700 630]
               [0 700] [200 700] [350 700] [500 700]
               [700 700] [800 700] [1000 700]
               [0 750]]
    :code ['(defn Kinetic-Energy
              velocity
              (:tiles/infix (* (:tiles/infix (/ 'm 2))
                               (square velocity))))
           '(defn Potential-Energy
              hight
              (:tiles/infix (* (:tiles/infix (* 'm 'g)) hight)))
           '(defn Lagrangian
              [[time [_ hight] velocity]]
              (:tiles/infix (- (Kinetic-Energy velocity)
                               (Potential-Energy hight))))
           'Rectangular-Angle
           '(defn :tiles/slot
              :tiles/slot
              :tiles/slot)
           [:tiles/slot]
           [:tiles/slot :tiles/slot] '_ [:tiles/slot] 'angle
           '(:tiles/vert
             (up :tiles/slot :tiles/slot))
           ''l '(:tiles/infix (* :tiles/slot :tiles/slot))
           '(sin :tiles/slot) 'angle
           ''h '(:tiles/infix (- :tiles/slot :tiles/slot))
           ''l '(:tiles/infix (* :tiles/slot :tiles/slot))
           '(cos :tiles/slot) 'angle

           '(compose :tiles/slot :tiles/slot)
           'Lagrangian
           '(F->C :tiles/slot)
           'Rectangular-Angle
           '(up :tiles/slot)
           '(literal-function :tiles/slot)
           ''phi
           '[:div>tex
             (((Lagrange-equations _Lagrangian)
               (_up (_literal-function 'q_x)
                    (_literal-function 'q_y)))
              't)]]
    :solpos-yx [[0 0]
                [150 0]
                [300 0]
                [470 0]
                [750 0]]
    :solution ['(:tiles/keep (defn Kinetic-Energy
                               velocity
                               (:tiles/infix (* (:tiles/infix (/ 'm 2))
                                                (square velocity)))))
               '(:tiles/keep (defn Potential-Energy
                               hight
                               (:tiles/infix (* (:tiles/infix (* 'm 'g)) hight))))
               '(:tiles/keep (defn Lagrangian
                               [[time [_ hight] velocity]]
                               (:tiles/infix (- (Kinetic-Energy velocity)
                                                (Potential-Energy hight)))))
               '(defn Rectangular-Angle
                  [[_ [angle]]]
                  (:tiles/vert
                   (up (:tiles/infix (* 'l (sin angle)))
                       (:tiles/infix (- 'h (:tiles/infix (* 'l (cos angle))))))))
               '(:tiles/keep [:div>tex
                              (((Lagrange-equations
                                 (compose Lagrangian (F->C Rectangular-Angle)))
                                (up (literal-function 'phi)))
                               't)])]}
   {:lable :pendulum-final
    :description
    [:div
     [:p "You came a long way to this final result. Maybe you skipped a view steps. Running this workspace yields the equation of motion for the driven pendulum."]
     [:p "You right-click and inspect the block \"(Hight-of-Pivot time)\". This demonstrates another very general result: blocks change their type during the course of the program."]
     [:p "In this last step, you introduced the function Hight-of-Pivot as a driver to the pendulum. It is an arbitrary function of time. The replacement is easily done, but notice that the transformation of coordinates now becomes time dependent. And with this, the Lagrangian becomes time dependent in an explicit manner."]]
    :message-fn
    (fn [_ ifo _]
      (get
       {'(Hight-of-Pivot time)
        "Hight-of-Pivot can be of type Differential as well as of type Expression. You wonder what Differential means in this context. And: you search for numbers, as those connect the thory to experiment. But they seem to be quite elusive here. Nevertheless, you hope to recover more numbers in future quests. For now, you have reached the end of this one."}
       (last ifo)))
    :scroll [0 -400]
    :blockpos [[0 0]
               [0 150]
               [0 300]
               [100 450]
               [0 470] [100 500] [100 540] [200 540] [400 540] [500 540]
               [0 650] [500 680] [500 800] [700 800]
               [0 900]]
    :code ['(defn Kinetic-Energy
              velocity
              (:tiles/infix (* (:tiles/infix (/ 'm 2))
                               (square velocity))))
           '(defn Potential-Energy
              hight
              (:tiles/infix (* (:tiles/infix (* 'm 'g)) hight)))
           '(defn Lagrangian
              [[time [_ hight] velocity]]
              (:tiles/infix (- (Kinetic-Energy velocity)
                               (Potential-Energy hight))))
           'Hight-of-Pivot
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           'time
           '(:tiles/slot :tiles/slot) '(literal-function :tiles/slot) ''h 'time
           '(defn Rectangular-Angle
              [[__ [angle]]]
              (:tiles/vert
               (up (:tiles/infix (* 'l (sin angle)))
                   (:tiles/infix (- '_h (:tiles/infix (* 'l (cos angle))))))))
           'time
           '(Hight-of-Pivot :tiles/slot) 'time
           '[:div>tex
             (((Lagrange-equations
                (compose Lagrangian (F->C Rectangular-Angle)))
               (up (literal-function 'phi)))
              't)]]
    :solpos-yx [[0 0]
                [150 0]
                [300 0]
                [470 0]
                [470 400]
                [750 0]]
    :solution ['(:tiles/keep (defn Kinetic-Energy
                               velocity
                               (:tiles/infix (* (:tiles/infix (/ 'm 2))
                                                (square velocity)))))
               '(:tiles/keep (defn Potential-Energy
                               hight
                               (:tiles/infix (* (:tiles/infix (* 'm 'g)) hight))))
               '(:tiles/keep (defn Lagrangian
                               [[time [_ hight] velocity]]
                               (:tiles/infix (- (Kinetic-Energy velocity)
                                                (Potential-Energy hight)))))
               '(defn Hight-of-Pivot
                  time
                  ((literal-function 'h) time))
               '(defn Rectangular-Angle
                  [[time [angle]]]
                  (:tiles/vert
                   (up (:tiles/infix (* 'l (sin angle)))
                       (:tiles/infix (- (Hight-of-Pivot time)
                                        (:tiles/infix (* 'l (cos angle))))))))
               '(:tiles/keep [:div>tex
                              (((Lagrange-equations
                                 (compose Lagrangian (F->C Rectangular-Angle)))
                                (up (literal-function 'phi)))
                               't)])]}
   {:description [:p "End of chapter"]
    :solution ["End of Chapter"]
    :code ["End of Chapter"]}])

(def content {:tutorials e-vect :chapnames ["Driven Pendulum"]
              :chaps [(count e-vect)]})
