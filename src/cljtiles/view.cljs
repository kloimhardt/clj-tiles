(ns cljtiles.view
  (:require
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [goog.dom.forms :as gforms]
   [sci.core :as sci]
   ["blockly" :as blockly]
   [cljtiles.xmlparse-2 :as edn->code]
   [cljtiles.tutorials-0 :as t-0]
   [cljtiles.genblocks :as gb]
   [cljtiles.tutorials-sicm :as t-s]
   [cljs.reader :as edn]
   [clojure.walk :as w]
   [tubax.core :as sax]
   [reagent.core :as rc]
   [reagent.dom :as rd]
   [zprint.core :as zp]
   [cljtiles.tests :as tst]
   [cljtiles.sicm :as sicm]
   [cljtiles.blockly :as workspace!]))

(when workspace!/dev
  (print (tst/test-pure)))

(def tutorials (vec (concat t-0/vect t-s/vect )))

(defn countup [chaps vect]
  (let [d (- (count vect) (reduce + chaps))]
    (if (pos? d)
      (update chaps (dec (count chaps)) #(+ % d)) chaps)))

(def chaps
  (mapcat #(apply countup %)
          [[t-0/chaps t-0/vect] [t-s/chaps t-s/vect]]))

(def chapnames (concat t-0/chapnames t-s/chapnames))

(defn filldesc [desc vect]
  (if (empty? desc) (repeat (count vect) "") desc))

(def desc (mapcat #(apply filldesc %)
                  [[t-0/desc t-0/vect] [t-s/desc t-s/vect]]))

(defn fillscroll [scroll vect]
  (if (empty? scroll) (repeat (count vect) nil) scroll))

(def scroll (mapcat #(apply fillscroll %)
                    [[t-0/scroll t-0/vect] [t-s/scroll t-s/vect]]))

(def rocket-no 49)

(defn page->chapter [page-no]
  (- (count chaps) (count (filter #(> % page-no) (reductions + chaps)))))

(defn chapter->page [chap-no]
  (if (pos? chap-no) (nth (reductions + chaps) (dec chap-no)) 0))

(defn load-workspace [xml-text]
  (.. blockly/Xml
      (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-text))
                                    (.getMainWorkspace blockly))))

(defonce state (rc/atom nil))

(defonce app-state (rc/atom nil))

(defn set-scrollbar [x y]
  (when x
    (.. blockly -mainWorkspace (scroll x y))))

(defn goto-page! [page-no]
  (load-workspace (get tutorials page-no))
  (apply set-scrollbar (nth scroll page-no))
  (gforms/setValue (gdom/getElement "tutorial_no") page-no)
  (reset! state
          {:desc (nth desc page-no) :stdout [] :inspect [] :result nil :code nil :tutorial-no page-no})
  (reset! app-state 0))

(defn tutorial-fu [inc-or-dec]
  (fn []
    (let [el (gdom/getElement "tutorial_no")
          idx-old  (gstring/toNumber (gforms/getValue el))
          idx-new (inc-or-dec idx-old)
          idx (cond
                (< -1 idx-new (count tutorials)) idx-new
                (> 0 idx-new) 0
                (< (dec (count tutorials)) idx-new) (dec (count tutorials))
                (< -1 idx-old (count tutorials)) idx-old
                :else 0)]
      (goto-page! idx))))

(defn code->break-str [width edn-code]
  (apply str (interpose "\n" (map #(zp/zprint-str % width) edn-code))))

(defn part-str [width s]
  (apply str
         (interpose "\n"
                    (map (partial apply str)
                         (partition-all width s)))))

(defn my-str [e width]
  (let [f (fn [x]
            (if (nil? x) "nil" (str x)))]
    (if (seq? e)
      (part-str width (apply str (interpose " " (map f e))))
      (part-str width (f e)))))

(defn augment-code-fu [edn-code flat-code fn-code]
  (if (seq (filter #{(second fn-code)} flat-code))
    (into [] (cons fn-code edn-code))
    edn-code))

(defn augment-code [edn-code]
  (let [flat-code (flatten (w/postwalk #(if (map? %) (vec %) %) edn-code))]
    (-> edn-code
        (augment-code-fu flat-code
                         '(defn vec-rest "added by Blockly parser" [x]
                            (let [r (rest x)] (if (seq? r) (vec r) r))))
        (augment-code-fu flat-code
                         '(defn vec-cons "added by Blockly parser" [x coll]
                            (let [c (cons x coll)] (if (seq? c) (vec c) c)))))))

(def timer (atom nil))
(def counter (atom 0))

(defn stop-timer [msg]
  (js/clearInterval @timer)
  (reset! timer nil)
  (reset! counter 0)
  msg)

(defn start-timer [fu ms max msg]
  (when-not @timer
    (reset! timer
            (js/setInterval (fn []
                              (swap! counter inc)
                              (if (< @counter max)
                                (fu)
                                (stop-timer nil))) ms))
    msg))

(defn bindings [new-println tex-print tex-inspect]
  (merge
   sicm/bindings
   {'println new-println
    'tex tex-print
    'tex-inspect tex-inspect
    'app-state app-state
    'start-timer start-timer
    'stop-timer stop-timer}))

(defn run-code [edn-code error]
  (let [aug-edn-code (augment-code edn-code)
        str-width 41
        new-println
        (fn [& x] (swap! state #(update % :stdout conj (apply str x))) nil)
        tex-print
        (fn [& x] (swap! state #(update % :stdout conj
                                        (sicm/tex (last x)))) nil)
        tex-inspect (fn [x] (swap! state #(update % :inspect conj (str (sicm/kind? x)))) x)
        bindings2 (bindings new-println tex-print tex-inspect)
        cbr (code->break-str str-width aug-edn-code)
        _ (swap! state assoc :stdout [])
        _ (swap! state assoc :inspect [])
        erg (try (sci/eval-string cbr {:bindings bindings2})
                 (catch js/Error e (.-message e)))]
    (swap! state assoc
           :result (cond (some? erg) (my-str erg str-width)
                         (= "nil" (str (last edn-code))) "nil"
                         :else "")
           :code (if error "Cannot even parse the blocks" cbr)
           :edn-code aug-edn-code)))

(defn ^:export startsci [context]
  (let [xml-str (->> (.-mainWorkspace blockly)
                     (.workspaceToDom blockly/Xml)
                     (.domToPrettyText blockly/Xml))
        edn-xml (sax/xml->clj xml-str)
        inspect-id (when context (.-id (get context "block")))
        edn-code (edn->code/parse edn-xml {:id inspect-id
                                           :fun (:inspect-fn context)})]
    (run-code edn-code nil)))

(defn tutorials-comp []
  [:div
   [:button {:on-click (fn []
                         (.. js/navigator -clipboard readText
                                 (then (fn [c] (->> (edn/read-string (str "[" c "]"))
                                                    (apply gb/rpg)
                                                    load-workspace)))))}
    "Paste"]
   " "
   [:span
    [:select {:value (page->chapter (:tutorial-no @state))
              :on-change (fn [el]
                           (let [chap (gstring/toNumber
                                       (.. el -target -value))
                                 no (chapter->page chap)]
                             (goto-page! no)))}
     (map-indexed (fn [idx val] [:option {:key idx :value idx} val]) chapnames)]
    " "
    [:button {:on-click (tutorial-fu dec)} "<"]
    " "
    [:input {:read-only true :size (inc (* 2 (count (str (count tutorials)))))
             :value (str (inc (:tutorial-no @state)) "/" (count tutorials))}]
    " "
    [:button {:on-click (tutorial-fu inc)} ">"]
    " "
    [:button {:on-click #(startsci nil)} "Run"]
    ]])

(defn filter-defns [edn-code fu]
  (conj
   (vec (filter #(= (symbol "defn") (first %)) edn-code))
   (list fu)
   (last edn-code)))

(defn to-kw [edn-code sy]
  (cond
    (symbol? sy)
    (let [s (str sy)]
      (cond
        (= ":" (first s)) (keyword (subs s 1 (count s)))
        (= "nil" s) nil
        (= "@app-state" s) @app-state
        :else sy))
    (map? sy)
    (if (:on-click sy)
      (assoc sy
             :on-click
             #(run-code (filter-defns edn-code (:on-click sy))
                        nil))
      sy)
    (list? sy)
    (try (sci/eval-string (pr-str sy))
         (catch js/Error e (.-message e)))
    :else sy))

(defn transform-vec [vect edn-code]
  (w/postwalk #(to-kw edn-code %)
              vect))

(defn reagent-comp []
  (let [v? #(when (vector? %) %)
        last-vec (v? (last (:edn-code @state)))]
    (when (= (symbol ":div") (first last-vec))
      [:div
       (transform-vec last-vec (:edn-code @state))])))

(defn tex-comp [_]
  (let [rerender
        (fn [node]
          (try (.Queue js/MathJax.Hub
                       #js ["Typeset" (.-Hub js/MathJax) node])
               (catch js/Error e (println (.-message e)))))]
    (rc/create-class
     {:reagent-render
      (fn [txt] [:div txt])
      :component-did-mount
      (fn [this]
        (rerender (rd/dom-node this)))
      :component-did-update
      (fn [this]
        (rerender (rd/dom-node this)))})))

(defn description-comp []
  [:div {:style {:column-count 2}}
   [tex-comp (:desc @state)]])

(defn result-comp []
  (let [flex50 {:style {:flex "50%"}}]
    (if (seq (:inspect @state))
      [:div
       (map-indexed (fn [idx v]
                      ^{:key idx}[:div
                                  [tex-comp v]
                                  [:hr]])
                    (:inspect @state))]
      [:div
       [:div {:style {:display "flex"}}
        [:div flex50
         [:div
          (map-indexed (fn [idx v]
                         ^{:key idx}[tex-comp v])
                       (:stdout @state))]
         [:pre (:result @state)]]
        [:div flex50
         [:pre (:code @state)]]]
       [description-comp]
       ])))

(defn theview []
  [:div
   [tutorials-comp]
   [reagent-comp]
   [result-comp]])

(defn ^{:dev/after-load true} render []
  ((tutorial-fu identity))
  (rd/render [theview] (gdom/getElement "out")))

(defn ^{:export true} output []
  (workspace!/init startsci)
  (some-> (not-empty (.. js/window -location -search))
          js/URLSearchParams.
          (.get "page")
          js/parseInt
          dec
          goto-page!)
  (render))
