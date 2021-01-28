(ns cljtiles.tutorials-sicm3
  (:require [cljtiles.sicm :as sc]))

(def e-vect
  [{:description
    [:<>
     [:p "You came a long way to this final result. Maybe you skipped a view steps. Running this workspace yields the equation of motion for the driven pendulum."]
     [:p "Inspecting the block \"(Hight-of-Pivot time)\" demonstrates another very general result: blocks change their type during the course of the program."]]
    :error-message-fn
    (fn [ifo error msg-fn]
      (str "An error occured. Maybe you can rearrange things so that " (last ifo) " is called before the error occurs."))
    :message-fn
    (fn [ifo result]
      (get
        {'(Hight-of-Pivot time)
         "Hight-of-Pivot can be of type Differential as well as of type Expression. You wander what Differential means in this context. And: you search for numbers, as those connect the thory to experiment. But they seem to be very elusive, as you find all kinds of types here, but no numbers. It seems, the more abstract the things scientists talk about, the better they understand each other. Nevertheless, you hope to recover numbers in future quests."}
        (last ifo)))
    :scroll [0 -400]
    :blockpos [[0 0]
               [0 150]
               [0 300]
               [0 470]
               [400 470]
               [0 750]]
    :code
    ['(defn Kinetic-Energy
        velocity
        (* (/ 'm 2) (square velocity)))
     '(defn Potential-Energy
        hight
        (* (* 'm 'g) hight))
     '(defn Lagrangian
        [[time [_ hight] velocity]]
        (- (Kinetic-Energy velocity)
           (Potential-Energy hight)))
     '(defn Hight-of-Pivot
        time
        ((literal-function 'h) time))
     '(defn Rectangular-Angle
        [[time [angle]]]
        (:tiles/vert
         (up (* 'l (sin angle))
             (- (Hight-of-Pivot time) (* 'l (cos angle))))))
     '(tex "Title"
           (((Lagrange-equations
               (compose Lagrangian (F->C Rectangular-Angle)))
             (up (literal-function 'phi)))
            't))]}])

(def chapnames ["Pendulum restult"])
(def chaps [(count e-vect)])