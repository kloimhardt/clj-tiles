(ns cljtiles.tutorials-lagr)

(def bold {:style {:font-weight "bold"}})

(def content
  {:chapnames ["Lagrangian"]
   :chaps [31] #_(count (:tutorials content))
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
    {:blockpos [[0 0] [0 100]
                [0 200] [100 200] [100 250] [100 300]]
     :code [[:div>tex :tiles/slot]
            '((L-free-particle 'm) :tiles/slot)
            '(:tiles/vert (up :tiles/slot :tiles/slot :tiles/slot))
            ''t ''x ''v_x
            ]}
    {:blockpos [[0 0] [70 30] [150 30] [150 70] [150 150]
                [0 250]
                [0 350]]
     :code ['(def local :tiles/slot)
            '(:tiles/vert (up :tiles/slot
                             (:tiles/vert :tiles/slot)
                             (:tiles/vert :tiles/slot)))
            ''t
            '(:tiles/vert (up 'x 'y))
            '(:tiles/vert (up 'v_x 'v_y))
            '[:div>tex ((L-free-particle 'm) :tiles/slot)]
            'local
            ]}
    {:blockpos [[0 0] [0 250]
                [150 300] [350 300]
                [170 350] [350 350] [400 350]]
     :code ['(defn test-path
              t
               (:tiles/vert (up (+ (* 4 t) 7)
                                (+ (* 3 t) 5)
                                (+ (* 2 t) 1))))
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
            3
            '(:tiles/slot :tiles/slot)
            '(+ f1 (* :tiles/slot f2)) 4
            ]}
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
            '(map (make-varied-line :tiles/slot 2 3) [1 2 3 4]) 0.01


            ]}
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
            'varied-path
            ]}
    {:blockpos [[0 0] [100 50] [200 50] [0 250] [200 250]]
     :code ['(def derivative-of-sine :tiles/slot)
            '(D :tiles/slot)
            'sin
            '(derivative-of-sine :tiles/slot)
            ''t]}
    {:blockpos [[0 0] [0 250] [200 250]]
     :code ['(def derivative-of-sine (D sin))
            '(derivative-of-sine :tiles/slot)
            0]}
    {:blockpos [[0 0]
                [100 50] [300 50] [500 50]
                [100 100]
                [0 250] [400 250]]
     :code ['(def second-derivative-of-function-q :tiles/slot)
            '(:tiles/slot :tiles/slot)
            '(literal-function :tiles/slot) ''q
            '(square D)
            '[:div>tex (second-derivative-of-function-q :tiles/slot)]
            8]}
    {:blockpos [[0 0] [300 0]
                [120 50]
                [550 150]
                [100 200]]
     :code ['(square D)
            'sin
            '(:tiles/slot :tiles/slot)
            ''t
            '[:div>tex (:tiles/slot :tiles/slot)]]}
    {:blockpos [[0 0] [300 0]
                [0 100]]
     :code ['(literal-function :tiles/slot) ''q
            '[:div>tex (((square D) :tiles/slot) 't)]]}
    {:blockpos [[0 0] [200 0] [0 100]]
     :code ['(Lagrange-equations :tiles/slot)
            '(L-free-particle 'm)
            '[:div>tex ((:tiles/slot (literal-function 'q)) 't)]] }
    {:blockpos [[0 0]
                [200 50] [500 50]
                [200 120] [500 120]
                [200 190] [500 190]
                [0 300] [0 400]]
     :code ['(defn straight-line t
               (:tiles/vert (up :tiles/slot
                                :tiles/slot
                                :tiles/slot)))
            '(+ (* :tiles/slot t) 'a0) ''a
            '(+ (* 'b :tiles/slot) 'b0) 't
            '(+ (* 'c t) :tiles/slot) ''c0
            '[:div>tex (((Lagrange-equations (L-free-particle 'm)) :tiles/slot) 't)]
            'straight-line]}
    {:blockpos [[0 0] [200 0] [250 0]
                [0 100]]
     :code ['(L-harmonic :tiles/slot :tiles/slot) ''m ''k
            '[:div>tex (:tiles/slot (up 't 'x 'v_x))]]}
    {:blockpos [[0 0]
                [0 100]]
     :code ['(L-harmonic 'm 'k)
            '[:div>tex (((Lagrange-equations :tiles/slot)
                         (literal-function 'q))
                        't)]]}
    {:blockpos [[0 0] [200 50] [300 100] [350 100] [450 100] [600 100] [700 100]
                [0 200] [0 300]]
     :code ['(defn proposed-solution t (* 'A :tiles/slot))
            '(cos :tiles/slot)
            ''t '(* :tiles/slot :tiles/slot) ''omega '(+ :tiles/slot :tiles/slot) ''phi
            '[:div>tex (((Lagrange-equations (L-harmonic 'm 'k)) :tiles/slot) 't)]
            'proposed-solution]}
    {:blockpos [[0 0] [200 50] [250 50] [400 50]
                [0 150] [450 200]
                [0 300]]
     :code ['(def omega (sqrt :tiles/slot)) ''k '(/ :tiles/slot :tiles/slot) ''m
            '(defn proposed-solution t (* 'A (cos (+ (* t :tiles/slot) 'phi))))
            'omega
            '[:div>tex (((Lagrange-equations (L-harmonic 'm 'k)) proposed-solution) 't)]]}
    {:blockpos [[0 0] [150 50] [150 150]
                [0 250] [150 300] [300 300]
                [0 400] [150 400]]
     :code ['(def local (:tiles/vert (up 't
                                         (:tiles/vert :tiles/slot)
                                         (:tiles/vert :tiles/slot))))
            '(:tiles/vert (up 'x 'y))
            '(:tiles/vert (up 'v_x 'v_y))
            '(defn get-time tuple :tiles/slot)
            '(first :tiles/slot)
            'tuple
            '(get-time :tiles/slot)
            'local]}
    {:blockpos [[0 0] [150 50] [150 150]
                [0 250] [300 280] [400 280]
                [0 400]]
     :code ['(def local (:tiles/vert (up 1
                                         (:tiles/vert :tiles/slot)
                                         (:tiles/vert :tiles/slot))))
            '(:tiles/vert (up 2.1 2.2))
            '(:tiles/vert (up 3.1 3.2))

            '(defn get-time :tiles/slot
               (first tuple))
            [:tiles/slot]
            'tuple
            '(get-time local)]}
    {:blockpos [[0 0]
                [0 100] [100 130] [200 130] [350 130] [400 130] [500 130]
                [200 200]
                [0 300]]
     :code ['(def local
               (up 1 (up 2.1 2.2) (up 3.1 3.2)))
            '(defn get-time :tiles/slot
               :tiles/slot)
            [:tiles/slot]
            [:tiles/slot :tiles/slot :tiles/slot]
            't
            'position 'velocity
            't
            '(get-time local)]}
    {:blockpos [[0 0]
                [0 100] [300 130] [400 130] [500 130]
                [200 200]
                [0 300]]
     :code ['(def local
              (up 1 (up 2.1 2.2) (up 3.1 3.2)))
            '(defn get-y-velocity [[t position :tiles/slot]] :tiles/slot)
            [:tiles/slot :tiles/slot] 'v_x 'v_y
            'v_y
            '(get-y-velocity local)]}
    {:blockpos [[0 0] [100 50] [100 150]
                [0 250]]
     :code ['(:tiles/vert (up 't
                              (:tiles/vert :tiles/slot)
                              (:tiles/vert :tiles/slot)))
            '(:tiles/vert (up 'x 'y))
            '(:tiles/vert (up 'v_x 'v_y))
            '[:div>tex ((L-free-particle 'm) :tiles/slot)]
            ]}
    {:blockpos [[0 0] [150 20] [150 50] [150 80] [150 110]
                [0 250]]
     :code ['(:tiles/vert (up 't
                              (:tiles/vert (up :tiles/slot :tiles/slot))
                              (:tiles/vert (up :tiles/slot :tiles/slot))))
            ''r ''φ
            ''rdot ''φdot
            '[:div>tex ((L-free-particle 'm) ((F->C p->r) :tiles/slot))]
            ]}
    {:blockpos [[0 0] [0 200] [150 200]]
     :code ['[:div>tex (:tiles/slot (:tiles/vert (up 't
                                                     (:tiles/vert (up 'r 'φ))
                                                     (:tiles/vert (up 'rdot 'φdot)))))]
            '(F->C :tiles/slot) 'p->r]}
    {:blockpos [[0 0] [0 200] [150 200]]
     :code ['[:div>tex (:tiles/slot (:tiles/slot (up 't (up 'r 'φ) (up 'rdot 'φdot))))]
            '(F->C :tiles/slot) 'p->r]}
    {:description
     [:p "Some more tutorials are needed to make the following chapter understandable. For mitigation, solutions are provided. In any case, try the first page: it has an additional level of interactivity and is simple enough still."]
     :blockpos [[0 0]]
     :code []}

    ]})
