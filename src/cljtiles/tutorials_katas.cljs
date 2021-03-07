(ns cljtiles.tutorials-katas)

(def content
  {:chapnames ["Higher order"]
   :chaps [21] #_(count (:tutorials content))
   :tutorials
   [{:lable :higher-order
     :blockpos [[0 0] [100 0] [200 0] [300 0] [0 100]]
     :code ['(:tiles/vert (let :tiles/slot :tiles/slot))
            [:tiles/slot :tiles/slot]
            'x 2 '(square x)]}
    {:blockpos [[0 0] [100 0] [100 30] [100 60] [100 90] [0 150]]
     :code ['(:tiles/vert (let (:tiles/vert [:tiles/slot :tiles/slot :tiles/slot :tiles/slot])
                            :tiles/slot))
            'x 2
            'f 'square
            '(f x)]}
    {:blockpos [[0 0] [100 70] [300 70] [0 150] [0 200]]
     :code
     [
      '(defn call-that-with-3 some-function :tiles/slot)
      '(some-function :tiles/slot) 3
      'inc
      '(call-that-with-3 :tiles/slot)]}
    {:blockpos [[0 0] [150 50] [250 50] [400 50] [150 100] [350 100] [150 250] [250 250] [0 300]]
     :code
     [
      '(defn call-with-squared :tiles/slot :tiles/slot)
      [:tiles/slot :tiles/slot]
      'some-function 'number
      '(some-function :tiles/slot) '(square number)
      'inc 3
      '(call-with-squared :tiles/slot :tiles/slot)]}
    {:blockpos [[0 0] [0 50] [0 100]]
     :code
     ['(mapv :tiles/slot :tiles/slot)
      'inc
      [1 2]]}
    {:blockpos [[0 0] [0 50] [0 100]]
     :code
     ['(mapv :tiles/slot :tiles/slot)
      'str
      [1 2]]}
    {:blockpos [[0 0] [0 50] [150 50]]
     :code
     ['(mapv str :tiles/slot :tiles/slot)
      [1 2]
      [3 4]]}
    {:blockpos [[0 0] [0 100]]
     :code
     ['(mapv :tiles/slot [1 2] [3 4])
      '+
      ]}
    {:blockpos [[0 0] [0 100]]
     :code
     ['(mapv :tiles/slot [1 2] [3 4])
      'vector
      ]}
    {:blockpos [[0 0] [0 50] [50 50]]
     :code
     ['(str :tiles/slot :tiles/slot)
      3 4
      ]}
    {:blockpos [[0 0] [0 50] [100 50]]
     :code
     ['(apply :tiles/slot :tiles/slot)
      'str
      [3 4]
      ]}
    {:blockpos [[0 0] [0 50] [50 50]]
     :code
     ['(str :tiles/slot :tiles/slot 3 4)
      1 2
      ]}
    {:blockpos [[0 0] [0 50] [100 50] [200 50]]
     :code
     ['(apply str :tiles/slot :tiles/slot :tiles/slot)
      1 2 [3 4]
      ]}
    {:blockpos [[0 0] [0 50] [50 50] [100 50] [150 50]]
     :code
     ['(+ :tiles/slot :tiles/slot :tiles/slot :tiles/slot)
      1 2 3 4
      ]}
    {:blockpos [[0 0] [0 50] [0 100]]
     :code
     ['(apply :tiles/slot 1 2 :tiles/slot)
      '+
      [3 4]
      ]}
    {:blockpos [[0 0] [0 50] [0 100] [0 150] [150 150]]
     :code
     ['(apply str :tiles/slot :tiles/slot)
      "zero"
      [:tiles/slot :tiles/slot]
      [1 2] [3 4]
      ]}
    {:blockpos [[0 0] [0 100] [0 200]]
     :code
     ['(println "remember?" (mapv vector [1 2] [3 4]))
      '(apply mapv vector :tiles/slot)
      [[1 2] [3 4]]]}
    {:blockpos [[0 0] [50 0] [100 0] [150 0]
                [0 50] [100 50] [200 50]
                [0 100] [100 100] [200 100]]
     :code
     [1 2 3 4
      [:tiles/slot :tiles/slot] [:tiles/slot :tiles/slot] [:tiles/slot :tiles/slot]
      'vector 'mapv '(apply :tiles/slot :tiles/slot :tiles/slot)
      ]}
    {:blockpos [[0 0] [100 70] [200 70] [200 120]
                [0 200] [150 230] [400 230]
                [0 300] [150 300]]
     :code
     [
      '(defn this-returns-a-function
         text
         :tiles/slot)
      '(:tiles/vert (fn :tiles/slot :tiles/slot))
      'number
      '(str text number)
      '(def a-function :tiles/slot)
      '(this-returns-a-function :tiles/slot) "three="
      '(a-function :tiles/slot) 3
      ]}
    {:blockpos [[0 0] [0 300] [150 300]]
     :code
     ['(defn this-returns-a-function
        text
         (:tiles/vert (fn number (str text number))))
      '(:tiles/slot 3)
      '(this-returns-a-function "three=")
      ]}
    {:blockpos [[0 0] [300 30] [400 100] [0 230] [0 300] [100 300]  [250 300]]
     :code
     ['(defn this-is-of-higher-order
         [:tiles/slot text]
         (:tiles/vert (fn some-data (str text (some-function :tiles/slot))))
         )
      'some-function
      'some-data
      '((this-is-of-higher-order :tiles/slot :tiles/slot) :tiles/slot)
      'first "one=" [1 2]
      ]}
    #_{:blockpos [[0 0]
                [0 200] [50 200] [120 200] [200 200] [300 200] [450 200] [600 200] [700 200]
                [0 250] [200 250]
                [0 300] [200 300] [400 300] [600 300]
                [0 350] [200 350] [400 350]]
     :code
     ['(defn :tiles/slot :tiles/slot :tiles/slot)
      1 'text 'text 'first 'some-data 'some-data "one=" 2
      'some-function 'this-is-of-higher-order
      '(some-function :tiles/slot)
      '(:tiles/slot :tiles/slot)
      [:tiles/slot :tiles/slot] [:tiles/slot :tiles/slot]
      '(:tiles/vert (fn :tiles/slot :tiles/slot))
      '(str :tiles/slot :tiles/slot)
      '(this-is-of-higher-order :tiles/slot :tiles/slot)

      ]}
    ]})
