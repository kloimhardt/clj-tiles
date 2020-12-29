(ns cljtiles.tutorials-sicm
  (:require [cljtiles.genblocks :as gb]))

(def chaps [1])
(def chapnames ["Tdp"])

(def e-vect
  [
   [:div
    [:p "We start by creating a function Path-of-a-Free-Particle. Newtons first law states that in some inertial frame of reference, an object continues to move in space at a constant velocity. This movement takes time, so our function depends on time. It returns a vector of two elements because we choose our path to live in two dimensions."]
    [:p "
In familiar notation, the path is denoted by (note that \\(\\vec{x}\\) is a column vector with superscripted component-indizes, hence the name \"up\" in the code):
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

      \\]
It is a moving body which is at time \\(t=0\\) at point
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
     " (and always think of a sattelite when someone talks about \"inertial frame of reference\")"]

    ]
   [0 0]
   (gb/rpg [[0 0]
            [0 90]
            [0 130]
            [0 180]
            [0 250] [100 250] [250 250] [350 250] [500 250]
            [0 300] [100 300] [250 300] [350 300] [500 300]]
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           'Path-of-a-Free-Particle
           'time
           '(:tiles/vert (up :tiles/slot :tiles/slot))
           2
           '(+ :tiles/slot :tiles/slot)
           5
           '(* :tiles/slot :tiles/slot)
           'time
           3
           '(+ :tiles/slot :tiles/slot)
           4
           '(* :tiles/slot :tiles/slot)
           'time)
   "Run the program. The output does not convey much yet"
   [0 0]
   (gb/rpg [[0 0]]
     '(defn Path-of-a-Free-Particle
       time
       (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time))))))
   [:div
    [:p "The kinetic energy is half the mass times velocity squared. If we set the mass to \\(2kg\\) the formula becomes simpler. We know that the velocity of our free particle is constant. In the \\(x\\) direction it is \\(5 \\frac{m}{s}\\). But notice that the velocity here could be any function in time (not in space+time.)"]
    [:p "We define this kinetic enery as yet another function which is not directly connected to the first but needed. The kinetic energy is a function of velocity, \\(T=T(\\vec{v}) = \\frac{m}{2} |\\vec{v}|^2 = \\frac{m}{2}v^2\\). With \\(m=2\\) you get \\( T=v^2\\) and with \\(v=\\sqrt{5^2 + 4^2}\\frac{m}{s}\\), you get \\(T=41 \\frac{kg\\ m^2}{s^2}\\). So, \\(v\\) is a constant here, but will become a function of time \\(v=v(t)\\). That means you need to accept the fact that \\(T\\) will become a function of a function. And, to make no mistake, velocity never is a function of position but only of time."]]
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
   "Run the program. The output does not convey much yet"
   [0 0]
   (gb/rpg [[0 0] [0 170]]
           '(defn Path-of-a-Free-Particle
             time
             (:tiles/vert (up (+ 2 (* 5 time)) (+ 3 (* 4 time)))))
           '(defn Kinetic-Energy
              velocity
              (square velocity)))
   [:div
    [:p "Now for the Lagrangian function. For our free particle, the Lagrangian is just the kinetic energy. In general, a Lagrangian does not only depend on the velocity, but also on time and position. We are not using time and position here, but they nevertheless have to appear in the input vector."]
    [:p "The Lagrangian is a function of three parameters: position, velocity and time, \\( L=L(\\vec{x}, \\vec{v}, t) \\), so the Lagrangian is in the most general case a function of two functions and a number. In this tutorial, as in most cases, \\(L\\) will never depend on the parameter time. At the moment, \\(L\\) depends only on the  velocity, \\(L=L(\\vec{v})\\)."]
    [:p "You come to the conclusion: The Lagrangian is a function of the parameter time which, within this tutorial, will never depend on this parameter time and, at the moment, it does not depend on time at all. This sounds paradoxical and shows the limits of usual mathematical notation and natural language usage. Nevertheless, the conclusion is right because that is what the code of the program actually says."]]
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
   "Run the program. The output does not convey much yet"
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
    [:p "The lagrangian equations are differential equations. They are constructed out of the Lagrangian function. The differential equations are applied to some path function. Here we use our specific Path-of-a-Free-Particle. The result is in general a function of time. We set the time to ten seconds here. If the path we fed into the equtions is a viable physical path, the result shoud be a very special function: it should be the zero vector."]]
   [0 -170]
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
           '10
           )
   "Print the result and find out whether you indeed see the zero vector."
   [0 -170]
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
                 10))
           )
   "You replace the Path-of-a-Free-Particle. You create a vector of two arbitrary funtions. Call them q_x and q_y. Then do the replacement of the specific path with the arbitrary path. After running the workspace again, you see the general equations of motion for the free particle."
   [0 -250]
   (gb/rpg [[0 0] [0 170] [0 280] [0 420]
            [0 550] [0 600] [200 600] [350 600] [550 600]]
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
           'q_y
           )
   "hui"
   [0 -250]
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
                 10))
           )
   ])

(def desc (reverse (take-nth 3 e-vect)))
(def scroll (reverse (take-nth 3 (rest e-vect))))
(def vect (reverse (take-nth 3 (rest (rest e-vect)))))
