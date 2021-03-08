(ns cljtiles.tutorials-lagr)

(def bold {:style {:font-weight "bold"}})

(def content
  {:chapnames ["Lagrangian"]
   :chaps [29] #_(count (:tutorials content))
   :tutorials
   [{:description
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
      0 \\\\
      2 \\\\
      4
      \\end{pmatrix}
=
      \\begin{pmatrix}
      1 \\\\
      5 \\\\
      9
      \\end{pmatrix}

      \\]"]]
     :blockpos [[0 0] [0 100]
                [0 250]
                [0 300] [150 300]
                [0 350] [150 350]
                ]
     :code [[:div>tex :tiles/slot]
            '(+ (:tiles/vert (up :tiles/slot 3 5)) (:tiles/vert (up :tiles/slot :tiles/slot (square 2))))
            1
            '(sin :tiles/slot) ''pi
            '(sqrt :tiles/slot) 4
            ]}
    {:blockpos [[0 0] [400 50] [400 100]
                [150 170] [400 170]
                [0 300] [150 300]]
     :code ['(defn test-path
               time
               (:tiles/vert (up (+ (* 4 time) :tiles/slot)
                                (+ (* :tiles/slot time) 5)
                                :tiles/slot)))
            7 3
            '(+ (* 2 :tiles/slot) 1) 'time
            '(test-path :tiles/slot) 10

            ]}
    {:scroll [0 0]
     :blockpos [[0 0] [0 100]]
     :code [['tex "sin^2{x} + cos^2{x} = 1"]
            [:div>b "This is bold text in HTML and the formula is LaTeX" {:tiles/numslot 0}]
            ]
     }
    {:blockpos [[0 0]
                [0 300] [250 300]]
     :code ['(defn test-path
               time
               (:tiles/vert (up (+ (* 4 time) 7)
                                (+ (* 3 time) 5)
                                (+ (* 2 time) 1))))
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
                [170 350]]
     :code ['(defn test-path
              t
               (:tiles/vert (up (+ (* 4 t) 7)
                                (+ (* 3 t) 5)
                                (+ (* 2 t) 1))))
            '(Lagrangian-action :tiles/slot :tiles/slot 0 10)
            '(L-free-particle :tiles/slot) 3
            'test-path
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
     :code ['(defn straight-line time
               (:tiles/vert (up :tiles/slot
                                :tiles/slot
                                :tiles/slot)))
            '(+ (* :tiles/slot time) 'a0) ''a
            '(+ (* 'b :tiles/slot) 'b0) 'time
            '(+ (* 'c time) :tiles/slot) ''c0
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
    {:blockpos [[0 0] [200 50] [250 100] [350 100] [450 100] [600 100] [700 100]
                [0 200] [0 300]]
     :code ['(defn proposed-solution time (* 'A :tiles/slot))
            '(cos :tiles/slot)
            ''omega '(* :tiles/slot :tiles/slot) 'time '(+ :tiles/slot :tiles/slot) ''phi
            '[:div>tex (((Lagrange-equations (L-harmonic 'm 'k)) :tiles/slot) 't)]
            'proposed-solution]}
    {:blockpos [[0 0] [200 50] [250 50] [400 50]
                [0 150] [500 200]
                [0 300]]
     :code ['(def omega (sqrt :tiles/slot)) ''k '(/ :tiles/slot :tiles/slot) ''m
            '(defn proposed-solution time (* 'A (cos (+ (* :tiles/slot time) 'phi))))
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
    {:blockpos [[0 0] [450 30] [550 30]
                [0 100] [450 130] [500 130] [550 130] [650 130]
                [200 250] [350 250]
                [0 300]
                ]
     :code ['(def local
               (up 't (up :tiles/slot :tiles/slot) (up 'v_x 'v_y)))
            ''x ''y
            '(defn r->p
              [[_ [:tiles/slot :tiles/slot :tiles/slot :tiles/slot] _]]
              (up (sqrt (square :tiles/slot)) (atan (/ :tiles/slot x))))
            'x 'y :as 'q
            'q 'y
            '[:div>tex (r->p local)]]}
    {:blockpos [[0 0] [0 100] [0 270]
                [0 330] [0 400]]
     :code ['(def local
              (up 't (up 'x 'y) (up 'v_x 'v_y)))
            '(defn r->p
              [[_ [x y :as q] _]]
              (up (sqrt (square q)) (atan (/ y x))))
            '(F->C :tiles/slot)
            '[:div>tex (:tiles/slot local)]
             'r->p]}
    {:blockpos [[0 0] [200 50] [200 80] [200 110] [200 140]
                [0 270] [0 330] [0 400]]
     :code ['(def local (:tiles/vert (up 't
                                         (:tiles/vert (up :tiles/slot :tiles/slot))
                                         (:tiles/vert (up :tiles/slot :tiles/slot)))))
            ''r ''φ
            ''rdot ''φdot
            '(F->C :tiles/slot)
            '[:div>tex (:tiles/slot local)]
            'p->r
            ]}
    {:blockpos [[0 0] [0 200]
                [0 300] [300 300]]
     :code ['(def local
               (:tiles/vert (up 't (:tiles/vert (up 'r 'φ)) (:tiles/vert (up 'rdot 'φdot)))))
            '[:div>tex (:tiles/slot :tiles/slot)]
            '(L-free-particle 'm)
            '((F->C p->r) local)
            ]}
    {:blockpos [[0 0]
                [0 180] [150 230] [350 230] [550 230]
                [0 350]
                ]
     :code ['(def local
               (:tiles/vert (up 't (:tiles/vert (up 'r 'φ)) (:tiles/vert (up 'rdot 'φdot)))))
            '(defn L-free-particle-polar mass
               :tiles/slot)
            '(compose :tiles/slot :tiles/slot)
            '(L-free-particle mass) '(F->C p->r)
            '[:div>tex ((L-free-particle-polar 'm) local)]
            ]}
    {:blockpos [[0 0]
                [200 30] [400 30]
                [200 70] [400 70]
                [0 120]
                [0 250]
                [0 350] [300 350] [400 350]
                ]
     :code ['(def path
               (:tiles/vert (up :tiles/slot :tiles/slot)))
            '(literal-function :tiles/slot) ''r
            '(literal-function :tiles/slot) ''φ
            '(defn L-free-particle-polar mass
               (compose (L-free-particle mass) (F->C p->r)))
            '[:div>tex (((Lagrange-equations :tiles/slot) :tiles/slot) :tiles/slot)]
            '(L-free-particle-polar 'm)
            'path
            ''t
            ]}
    {:blockpos [[0 0] [50 0] [100 0] [150 0] [200 0] [300 0]
                [0 50] [200 50] [400 50] [600 50]
                [0 100] [200 100] [300 100] [400 100]
                [0 150] [200 150]]
     :code [''m ''r ''φ ''t 'p->r :div>tex
            '(literal-function :tiles/slot)
            '(literal-function :tiles/slot)
            '(L-free-particle :tiles/slot)
            '(F->C :tiles/slot)
            '(Lagrange-equations :tiles/slot)
            '(:tiles/slot :tiles/slot)
            '(:tiles/slot :tiles/slot)
            '[:tiles/slot :tiles/slot]
            '(:tiles/vert (up :tiles/slot :tiles/slot))
            '(compose :tiles/slot
                     :tiles/slot)
            ]}]})
