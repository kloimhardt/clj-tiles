(ns cljtiles.tutorials-lagr)

(def content
  {:chapnames ["Lagrangian"]
   :chaps [5] #_(count (:tutorials content))
   :tutorials
   [{:blockpos [[0 0] [150 170] [0 250]
                [150 300] [350 300]
                [170 350] [350 350] [400 350]]
     :code ['(defn test-path
              t
               (:tiles/vert (up (+ (* 4 t) 7)
                                (+ (* 3 t) 5)
                                :tiles/slot)))
            '(+ (* 2 t) 1)
            '(Lagrangian-action :tiles/slot :tiles/slot :tiles/slot :tiles/slot)
            '(L-free-particle :tiles/slot) 3
            'test-path 0 10
            ]}
    {:blockpos [[0 0] [0 150] [100 150] [200 150] [0 250] [0 300] [0 350]]
     :code ['(defn make-η
              [:tiles/slot t1 t2]
               (:tiles/vert (fn t
                              (* (- t t1) (- t t2) :tiles/slot))))
            'ν '(ν :tiles/slot) 't
            'square
            '(map (make-η :tiles/slot  1 4) [1 2 3 4])
            ]}
    {:blockpos [[0 0] [300 0] [600 0] [0 150] [0 200] [300 200]]
     :code ['(defn f1 t (* 1 t))
            '(defn f2 t (* 2 t))
            '(defn f3 t (* 3 t))
            '(:tiles/slot :tiles/slot)
            '(+ f1 (* f2 f3)) 4
            ]}
    {:blockpos [[0 0]
                [0 150] [200 170]
                [0 250]
                [300 280] [650 280] [700 280]
                [300 320] [380 320] [500 320]
                [0 400]]
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
            '(map (make-varied-line 0.01 2 3) [1 2 3 4])


            ]}
    {:blockpos [[0 0]]
     :code []}

    ]})
