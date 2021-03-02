(ns cljtiles.tutorials-lagr)

(def bold {:style {:font-weight "bold"}})

(def content
  {:chapnames ["Lagrangian"]
   :chaps [7] #_(count (:tutorials content))
   :tutorials
   [{:scroll [0 0]
     :blockpos [[0 0] [0 100]]
     :code [['tex "sin^2{x} + cos^2{x} = 1"]
            [:div>b "This is bold text in HTML and the formula is LaTeX" {:tiles/numslot 0}]
            ]
     }
    {:description
     [:div
      [:div bold "Explanation"]
      [:p "A mathematical vector is created with the function \"up\". In usual notation this workspace reads:
   \\[
      \\begin{pmatrix}
      1 \\\\
      3 \\\\
      5
      \\end{pmatrix}
+
      \\begin{pmatrix}
      2 \\\\
      4 \\\\
      6
      \\end{pmatrix}
=
      \\begin{pmatrix}
      3 \\\\
      7 \\\\
      11
      \\end{pmatrix}

      \\]"]]
     :blockpos [[0 0] [0 100] [0 200]]
     :code [[:div>tex :tiles/slot]
            '(+ (:tiles/vert (up :tiles/slot 3 5)) (:tiles/vert (up 2 4 6)))
            1]}
    {:blockpos [[0 0] [150 170] [400 170]
                [0 300] [150 300]]
     :code ['(defn test-path
               t
               (:tiles/vert (up (+ (* 4 t) 7)
                                (+ (* 3 t) 5)
                                :tiles/slot)))
            '(+ (* 2 :tiles/slot) 1) 't
            '(test-path :tiles/slot) 10

            ]}
    {:blockpos [[0 0]
                [0 300] [250 300]]
     :code ['(defn test-path
               t
               (:tiles/vert (up (+ (* 4 t) 7)
                                (+ (* 3 t) 5)
                                (+ (* 2 t) 1))))
            '[:div>tex (test-path :tiles/slot)] ''t

            ]}
    {:code [;;'((L-free-particle 'm) (->local 't (up 'x 'y 'z) (up 'v_x 'v_y 'v_z)))
            '((L-free-particle 'm)
              (->local 't (up 'x 'y 'z) (up 'v_x 'v_y 'v_z)))
            ]}
    {:blockpos [[0 0] [150 170] [0 250]
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
