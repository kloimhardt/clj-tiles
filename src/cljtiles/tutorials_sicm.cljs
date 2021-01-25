(ns cljtiles.tutorials-sicm
  (:require [cljtiles.genblocks :as gb]))

(defn make-coords [rows cols]
  (for [y (range rows)
        x (range cols)]
    [(* x 300) (* y 150)]))

(def bold {:style {:font-weight "bold"}})
(def e-vect
  ["You run the program, but the output does not convey much yet."
   [0 0]
   (gb/rpg [[0 0]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time))))))
   [:div
    [:div bold "Description"]
    [:p "The kinetic energy is half the mass times velocity squared. If we set the mass to \\(2kg\\) the formula becomes simpler. We know that the velocity of our free particle is constant. In the \\(x\\) direction it is \\(5 \\frac{m}{s}\\). But notice that the velocity here could be any function in time (not in space+time.)"]
    [:div bold "Explanation"]
    [:p "You define the kinetic energy as another function, which is not yet connected to the function defined before. The kinetic energy is a function of velocity, \\(T=T(\\vec{v}) = \\frac{m}{2} |\\vec{v}|^2 = \\frac{m}{2}v^2\\). With \\(m=2\\) you get \\( T=v^2\\) and with \\(v=\\sqrt{5^2 + 4^2}\\frac{m}{s}\\), you get \\(T=41 \\frac{kg\\ m^2}{s^2}\\). Again, \\(v\\) is a constant here, as we are dealing with the free particle. But \\(v\\) will become a function of time \\(v=v(t)\\). That means you need to accept the fact that \\(T\\) will become a function of a function. And, to make no mistake, velocity never is a function of position but only of time."]]
   [0 0]
   (gb/rpg [[0 0] [0 170] [0 260] [0 300] [0 340] [150 340]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           'Kinetic-Energy
           'velocity
           '(square :tiles/slot)
           'velocity)
   "You run the program, but the output does not convey much yet."
   [0 0]
   (gb/rpg [[0 0] [0 170]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity)))
   [:div
    [:div bold "Description"]
    [:p "Now for the Lagrangian function. For our free particle, the Lagrangian is just the kinetic energy. In general, a Lagrangian does not only depend on the velocity, but also on time and position. We are not using time and position here, but they nevertheless have to appear in the input vector."]
    [:div bold "Explanation"]
    [:p "The term \"input vector\" is maybe misleading, it should better be called \"arguments of the function\" or \"function parameters\"."]
    [:p "The Lagrangian always is a function of three parameters: time, position and velocity, \\( L=L(t, \\vec{x}, \\vec{v}) \\), so the Lagrangian is a function of a number and in general two functions. At the moment, \\(L\\) only depends on the velocity, \\(L=L(\\vec{v})\\)."]
    [:p "You come to the conclusion: The Lagrangian always is a function of the parameter time which at the moment does not depend on time at all. This sounds paradoxical only due to the limits of usual mathematical notation and natural language usage. The conclusion is completely right. Time appears in the arguments of the function, but not in its body."]]
   [0 0]
   (gb/rpg [[0 0] [0 170] [0 280] [150 280] [150 330] [350 330]
            [300 250] [400 250] [550 250] [650 250] [750 250]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
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
           'velocity)

   "You run the program, but the output does not convey much yet."
   [0 0]
   (gb/rpg [[0 0] [0 170] [0 280]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity)))
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
If you now apply this operator to the Path-of-a-Free-Particle, the zero row vector is obtained becuase Path-of-a-Free-Particle is only linear in time and thus its second derivative is zero. And the above program shows exactly this. Even if you do not understand the insanely complex last block, you still have the consolation that it works."]
[:p
"The mathematical expressions here can only be some kind of guide. To understand how it all fits together, you have to play with the blocks. A full understanding is not needed for playing and investigating. However, the theoretical underpinnings can be found in " [:a {:href "ftp://publications.ai.mit.edu/ai-publications/2002/AIM-2002-018.ps"} "this paper"]
"."]
    ]
   [0 -150]
   (gb/rpg [[0 0] [0 170] [0 280]
            [0 420]
            [0 500] [150 500] [300 500]
            [400 450] [450 400] [600 350]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           '(tex "Title" :tiles/slot)
           '(:tiles/slot :tiles/slot)
           '(:tiles/slot :tiles/slot)
           '(Lagrange-equations :tiles/slot)
           'Lagrangian
           'Path-of-a-Free-Particle
           '10)
   "Print the result and find out whether you indeed see the zero vector."
   [0 -150]
   (gb/rpg [[0 0] [0 170] [0 280]
            [0 420]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   Path-of-a-Free-Particle)
                  10)))
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
my not be linear in time."
     ]
     [:p "The actual result is
\\[
\\left[ 2 \\frac{d^2 q_x(t)}{dt^2}\\Bigg|_{t=10}\\ \\ 2 \\frac{d^2 q_y(t)}{dt^2}\\Bigg|_{t=10}\\right]
\\]
"]
    ]
   [0 -150]
   (gb/rpg [[0 0] [0 170] [0 280] [0 420]
            [0 500]
            [0 550] [200 550] [350 550] [550 550]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   Path-of-a-Free-Particle)
                  10))
           '(up :tiles/slot :tiles/slot)
           '(literal-function :tiles/slot)
           'q_x
           '(literal-function :tiles/slot)
           'q_y)
   "Run the workspace to see the general equations of motion for the free particle."
   [0 -150]
   (gb/rpg [[0 0] [0 170] [0 280] [0 420]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  10)))
   [:div
    [:div bold "Description"]
    [:p
     "Now, you want to make the equations prettier. First, you replace 10 by some time \\(t\\),
second you introduce the mass \\(m\\) into the kinetic energy. You throw away the specific path, it is not needed anymore. After running, you see Newtons equations of motion for the two dimensional free particle in their standard form." ]
    [:div bold "Explanation"]
    [:p "The result now is
\\[
\\left[ m \\frac{d^2 q_x(\\tau)}{d\\tau^2}\\Bigg|_{\\tau=t}\\ \\ m \\frac{d^2 q_y(\\tau)}{d\\tau^2}\\Bigg|_{\\tau=t}\\right]
\\]
"]]
   [0 -150]
   (gb/rpg [[0 0] [0 170]
            [350 170] [400 170] [550 170]
            [350 230]
            [0 280]
            [900 380]
            [0 420]]
           '(defn Path-of-a-Free-Particle
              time
              (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity))
           ''m
           '(/ :tiles/slot :tiles/slot)
           '2
           '(* :tiles/slot :tiles/slot)
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           ''t
           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  10)))
   "After running, you see Newtons equations of motion for the two dimensional free particle in their standard form."
   [0 0]
   (gb/rpg [[0 0] [0 150] [0 300]]
           '(defn Kinetic-Energy
              velocity
              (* (/ 'm 2) (square velocity)))
           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  't)))
   "Now you model a particle in the gravitational field by introducing the potential energy. It depends on
the hight of the particle above ground. It is \\(m \\times g \\times hight\\), where \\(g\\) is \\(9.81 \\frac{m}{s^2}\\), the acceleration due to the gravity of the earth. The Lagrangain is kintic energy minus potential energy. And the Lagrangian now depends on the hight."
   [0 0]
   (gb/rpg [[0 0] [0 150]
            [100 120] [100 170]
            [100 220] [180 220] [300 220] [380 220] [500 220]
            [0 300]
            [450 300] [550 300] [600 300]
            [300 400] [450 400] [650 400]
            [0 500]]
           '(defn Kinetic-Energy
              velocity
              (* (/ 'm 2) (square velocity)))
           '(defn :tiles/slot
              :tiles/slot
              :tiles/slot)
           'Potential-Energy
           'hight
           ''m
           '(* :tiles/slot :tiles/slot)
           ''g
           '(* :tiles/slot :tiles/slot)
           'hight

           '(defn Lagrangian
              [[time position velocity]]
              (Kinetic-Energy velocity))
           [:tiles/slot :tiles/slot]
           '_
           'hight
           '(- :tiles/slot :tiles/slot)
           '(Potential-Energy :tiles/slot)
           'hight

           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  't)))
   "Run the workspace and Newtons equations of motion for the particle in the homogeneous gravitational field are delivered."
   [0 0]
   (gb/rpg [[0 0]
            [0 150]
            [0 300]
            [0 500]]
           '(defn Kinetic-Energy
              velocity
              (* (/ 'm 2) (square velocity)))
           '(defn Potential-Energy
              hight
              (* (* 'm 'g) hight))
           '(defn Lagrangian
              [[time [_ hight] velocity]]
              (- (Kinetic-Energy velocity)
                 (Potential-Energy hight)))
           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  't)))
   "To get the equations for the pendulum, you introduce a transformation of coordiantes. The new single coordinate is the angle between the rod and its position at rest. So the old coordinates become dependent on each other. The horizontal position x of the bob is \\( l \\times \\sin(angle)\\), where l is the length of the pendulum. The hight of the bob is \\(h - l \\times \\cos(angle)\\) (h is the hight of the pivot). The Lagangian of the pendulum is obtained by prepending the transformation. In this function composition, the Lagrangian of the independently moving particle is reused completely unchanged. The new Lagrangian equations are now applied to some function \\(\\phi\\). You can do the functional replacements using the blcks you just built."
   [0 -400]
   (gb/rpg [[0 0]
            [0 150]
            [0 300]
            [100 450]
            [0 470] [100 500] [200 500] [300 500] [400 500] [500 500]
            [100 570] [200 570] [300 570] [400 570] [500 570]
            [200 630] [300 630] [400 630] [500 630] [600 630] [700 630]
            [0 700] [200 700] [350 700] [500 700]
            [700 700] [800 700] [1000 700]
            [0 750]
            ]
           '(defn Kinetic-Energy
              velocity
              (* (/ 'm 2) (square velocity)))
           '(defn Potential-Energy
              hight
              (* (* 'm 'g) hight))
           '(defn Lagrangian
              [[time [_ hight] velocity]]
              (- (Kinetic-Energy velocity)
                 (Potential-Energy hight)))
           'Rectangular-Angle
           '(defn :tiles/slot
              :tiles/slot
              :tiles/slot)
           [:tiles/slot]
           [:tiles/slot :tiles/slot] '_ [:tiles/slot] 'angle
           '(:tiles/vert
            (up :tiles/slot :tiles/slot))
           ''l '(* :tiles/slot :tiles/slot) '(sin :tiles/slot) 'angle
           ''h '(- :tiles/slot :tiles/slot)
           ''l '(* :tiles/slot :tiles/slot)
           '(cos :tiles/slot) 'angle
           
           '(compose :tiles/slot :tiles/slot)
           'Lagrangian
           '(F->C :tiles/slot)
           'Rectangular-Angle
           '(up :tiles/slot)
           '(literal-function :tiles/slot)
           ''phi
           '(tex "Title"
                 (((Lagrange-equations Lagrangian)
                   (up (literal-function 'q_x)
                       (literal-function 'q_y)))
                  't))
           )
   "Run the workbook and get the equations for the pendulum delivered."
   [0 -400]
   (gb/rpg [[0 0]
            [0 150]
            [0 300]
            [0 470]
            [0 750]]
           '(defn Kinetic-Energy
              velocity
              (* (/ 'm 2) (square velocity)))
           '(defn Potential-Energy
              hight
              (* (* 'm 'g) hight))
           '(defn Lagrangian
              [[time [_ hight] velocity]]
              (- (Kinetic-Energy velocity)
                 (Potential-Energy hight)))
           '(defn Rectangular-Angle
              [[_ [angle]]]
              (:tiles/vert
               (up (* 'l (sin angle))
                   (- 'h (* 'l (cos angle))))))
           '(tex "Title"
                 (((Lagrange-equations
                     (compose Lagrangian (F->C Rectangular-Angle)))
                   (up (literal-function 'phi)))
                  't)))
   "In the last step, you introduce the function Hight-of-Pivot as a driver to the pendulum. It is an arbitrary funtion of time. The replacement is easily done, but notice that the transformation of coordinates now becomes time dependent. And with this, the Lagrangian becomes time dependent in an explicit manner."
   [0 -400]
   (gb/rpg [[0 0]
            [0 150]
            [0 300]
            [100 450]
            [0 470] [100 500] [100 540] [200 540] [400 540] [500 540]
            [0 650] [500 680] [500 800] [700 800]
            [0 900]]
           '(defn Kinetic-Energy
              velocity
              (* (/ 'm 2) (square velocity)))
           '(defn Potential-Energy
              hight
              (* (* 'm 'g) hight))
           '(defn Lagrangian
              [[time [_ hight] velocity]]
              (- (Kinetic-Energy velocity)
                 (Potential-Energy hight)))
           'Hight-of-Pivot
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           'time
           '(:tiles/slot :tiles/slot) '(literal-function :tiles/slot) ''h 'time
           '(defn Rectangular-Angle
              [[_ [angle]]]
              (:tiles/vert
               (up (* 'l (sin angle))
                   (- 'h (* 'l (cos angle))))))
           'time
           '(Hight-of-Pivot :tiles/slot) 'time
           '(tex "Title"
                 (((Lagrange-equations
                     (compose Lagrangian (F->C Rectangular-Angle)))
                   (up (literal-function 'phi)))
                  't)))
   ])

(def desc (take-nth 3 e-vect))
(def scroll (take-nth 3 (rest e-vect)))
(def vect (take-nth 3 (rest (rest e-vect))))

(def chapnames ["Pendulum inter"])
(def chaps [(count vect)])
