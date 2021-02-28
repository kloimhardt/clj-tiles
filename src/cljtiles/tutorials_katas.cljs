(ns cljtiles.tutorials-katas)

(def content
  {:chapnames ["Higher order"]
   :chaps [14]
   :tutorials
   [{:blockpos [[0 0] [0 50] [0 100]]
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
    ]})
