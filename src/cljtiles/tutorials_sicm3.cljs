(ns cljtiles.tutorials-sicm3
  (:require [cljtiles.sicm :as sc]))

(def e-vect
  [{:description
    "Running the workspace yields the equation for the driven pendulum."
    :error-message-fn
    (fn [ifo error msg-fn]
      (str "An error occured. Maybe you can rearrange things so that " (last ifo) " is called before the error occurs."))
    :message-fn
    (fn [ifo result]
      (get
        {'(Hight-of-Pivot time)
         "We see that the hight of the pivot can be of type Differential as well as of type Expression. I think the most important type is still the number as it connects the theory to experiment. But this type seems to be the most elusive one, as we have all kinds of types in this final result, but no numbers."}
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
