(ns cljtiles.tutorials-appen)

(def content
  {:chapnames ["Appendix"]
   :chaps [6] #_(count (:tutorials content))
   :tutorials
   [{:blockpos [[0 0] [0 150] [100 150] [200 150] [0 250] [0 300] [0 350]]
     :code ['(defn make-η
               [:tiles/slot t1 t2]
               (:tiles/vert (fn t
                              (* (- t t1) (- t t2) :tiles/slot))))
            'ν '(ν :tiles/slot) 't
            'square
            '(map (make-η :tiles/slot  1 4) [1 2 3 4])]}
    {:blockpos [[0 0] [300 0] [600 0] [0 150] [0 200] [300 200]]
     :code ['(defn f1 t (* 1 t))
            '(defn f2 t (* 2 t))
            3
            '(:tiles/slot :tiles/slot)
            '(+ f1 (* :tiles/slot f2)) 4]}
    {:blockpos [[0 0]
                [0 150] [200 170]
                [0 250]
                [300 280] [650 280] [700 280]
                [300 320] [380 320] [500 320]
                [0 380] [600 380]]
     :code ['(defn make-η
               [ν t1 t2]
               (:tiles/vert (fn t (* (- t t1) (- t t2) (ν t)))))
            '(defn line :tiles/slot t)
            't
            '(defn make-varied-line [:tiles/slot :tiles/slot :tiles/slot]
               (+ line :tiles/slot))
            'ε 't0 't1
            'ε
            '(* :tiles/slot :tiles/slot)
            '(make-η square t0 t1)
            '(map (make-varied-line :tiles/slot 2 3) [1 2 3 4]) 0.01]}

    {:scroll [0 -110]
     :blockpos [[0 0] [0 150]
                [0 270] [600 350]
                [0 420] [500 450]
                [0 500] [500 500]]
     :code ['(defn make-η
               [ν t1 t2]
               (fn [t] (* (- t t1) (- t t2) (ν t))))
            '(defn test-path
               t
               (up (+ (* 4 t) 7)
                   (+ (* 3 t) 5)
                   (+ (* 2 t) 1)))
            '(defn make-varied-path
               [ε t0 t1]
               (+ test-path (* ε (make-η :tiles/slot t0 t1))))
            '(up sin cos square)
            '(def varied-path (make-varied-path :tiles/slot 0 10)) 0.01
            '(Lagrangian-action (L-free-particle 3) :tiles/slot 0 10)
            'varied-path]}]})
