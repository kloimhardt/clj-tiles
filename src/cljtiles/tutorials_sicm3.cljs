(ns cljtiles.tutorials-sicm3
  (:require [cljtiles.sicm :as sc]))

(def e-vect
  [{:lable :pendulum-final
    :description
    [:<>
     [:p "You came a long way to this final result. Maybe you skipped a view steps. Running this workspace yields the equation of motion for the driven pendulum."]
     [:p "Inspecting the block \"(Hight-of-Pivot time)\" demonstrates another very general result: blocks change their type during the course of the program."]]
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
     '[html-tex
           (((Lagrange-equations
               (compose Lagrangian (F->C Rectangular-Angle)))
             (up (literal-function 'phi)))
            't)]]}])

(def chapnames ["Pendulum restult"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
