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
   [cljtiles.tutorials-sicm2 :as t-s2]
   [cljs.reader :as edn]
   [clojure.walk :as w]
   [tubax.core :as sax]
   [reagent.core :as rc]
   [reagent.dom :as rd]
   [zprint.core :as zp]
   [cljtiles.tests :as tst]
   [cljtiles.sicm :as sicm]
   [cljtiles.blockly :as workspace!]
   [cljtiles.code-analysis :as ca]))

(when workspace!/dev
  (print (tst/test-pure)))

(def chaps (concat t-0/chaps t-s/chaps t-s2/chaps))

(def chapnames (concat t-0/chapnames t-s/chapnames t-s2/chapnames))

(def tutorials_clj (map (fn [xml-code]
                       {:xml-code xml-code})
                     t-0/vect))

(def tutorials_scm (map (fn [description scroll xml-code]
                          {:description description
                           :scroll scroll
                           :xml-code xml-code})
                        t-s/desc
                        t-s/scroll
                        t-s/vect))

(defn generate-xml [pages]
  (map #(assoc % :xml-code (apply gb/rpg (:blockpos %) (:code %))) pages))

(def tutorials (concat tutorials_clj
                       tutorials_scm
                       (generate-xml t-s2/e-vect)))

(defn page->chapter [page-no]
  (- (count chaps) (count (filter #(> % page-no) (reductions + chaps)))))

(defn chapter->page [chap-no]
  (if (pos? chap-no) (nth (reductions + chaps) (dec chap-no)) 0))

(defn load-workspace [xml-text]
  (.. blockly/Xml
      (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-text))
                                    (.getMainWorkspace blockly))))

(defn append-to-workspace [xml-text]
  (.. blockly/Xml
      (appendDomToWorkspace (.. blockly/Xml (textToDom xml-text))
                            (.getMainWorkspace blockly))))

(defonce state (rc/atom nil))

(defn reset-state [tutorial-no]
  (reset! state
          {:desc (:description (nth tutorials tutorial-no)) :stdout [] :inspect [] :sci-error nil :result nil
           :code nil :edn-code nil
           :tutorial-no tutorial-no :reagent-error nil
           :modal-style-display "none"}))

(defonce app-state (rc/atom nil))

(defn set-scrollbar [x y]
  (when x
    (.. blockly -mainWorkspace (scroll x y))))

(defn goto-page! [page-no]
  (load-workspace (:xml-code (nth tutorials page-no)))
  (apply set-scrollbar (:scroll (nth tutorials page-no)))
  (gforms/setValue (gdom/getElement "tutorial_no") page-no)
  (reset-state page-no)
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
(defn my-str [x]
  (if (nil? x) "nil" (str x)))

(defn my-str-brk [e width]
  (if (seq? e)
    (part-str width (apply str (interpose " " (map my-str e))))
    (part-str width (my-str e))))

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
    workspace!/inspect-fn-sym tex-inspect
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
        _ (reset-state (:tutorial-no @state))
        erg (try (sci/eval-string cbr {:bindings bindings2})
                 (catch js/Error e (swap! state assoc :sci-error (my-str-brk (.-message e) str-width)) nil))]
    (swap! state assoc
           :result (cond (some? erg) (my-str erg)
                         (= "nil" (str (last edn-code))) "nil"
                         :else "")
           :code (if error "Cannot even parse the blocks" cbr)
           :edn-code aug-edn-code)))

(defn insert-inspect [edn-code inspect-form]
    (if inspect-form
      (concat (take 3 edn-code)
              (conj (drop 3 edn-code) inspect-form))
      edn-code))

(defn get-edn-code [xml-str inspect-id inspect-fn]
  (let [edn-xml (sax/xml->clj xml-str)
        eci (edn->code/parse edn-xml {:id inspect-id :fun inspect-fn})
        inspect-fn-name (when inspect-fn (first (inspect-fn 0)))
        ifo (ca/inspect-froms eci inspect-fn-name)]
    (if (not-every? nil? ifo)
      (let [ec (edn->code/parse edn-xml {:id nil :fun nil})]
        (map insert-inspect ec ifo))
      eci)))

(defn ^:export startsci [context]
  (let [xml-str (->> (.-mainWorkspace blockly)
                     (.workspaceToDom blockly/Xml)
                     (.domToPrettyText blockly/Xml))
        edn-code (get-edn-code xml-str
                               (when context (.-id (get context "block")))
                               (:inspect-fn context))]
    (run-code edn-code nil)))

(defn open-modal []
  (swap! state assoc :modal-style-display "block"))

(defn modal-comp []
  (let [textarea-element (atom nil)
        run-parser
        (fn  []
          (let [a (edn/read-string (str "[" (.-value @textarea-element) "]"))]
            (if (list? (first a))
              (run! (fn [c] (append-to-workspace (gb/rpg [] c))) a)
              (load-workspace (apply gb/rpg a)))))
        close-modal
        (fn []
          (set! (.-value @textarea-element) "")
          (swap! state assoc :modal-style-display "none"))]
    (fn []
      [:div {:id "myModal", :class "modal"
             :style {:display (:modal-style-display @state)}}
       [:div {:class "modal-content"}
        [:div
         [:textarea {:cols 80 :rows 10
                     :ref (fn [e] (reset! textarea-element e) (some-> e .focus))}]]
        [:button {:on-click #(do (run-parser) (close-modal))}
         "Insert"]
        " "
        (when-let [h (:hint (nth tutorials (:tutorial-no @state)))]
          (let [i (atom 0)]
            [:button {:on-click (fn []
                                  (set! (.-value @textarea-element)
                                        (get h @i))
                                  (swap! i inc))}
             "Hint"]))
        [:button {:style {:float "right"} :on-click close-modal}
         "Cancel"]]])))

(defn tutorials-comp []
  [:div
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
    [:button {:on-click #(startsci nil)} "Run"]]])

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

(defn is-last-div []
  (let [v? #(when (vector? %) %)
        last-vec (v? (last (:edn-code @state)))]
    (when (= (symbol ":div") (first last-vec))
      last-vec)))

(defn reagent-comp [last-vec]
  (transform-vec last-vec (:edn-code @state)))

(defn tex-comp [txt]
  [:div {:ref (fn [el] (try (.Queue js/MathJax.Hub
                                    #js ["Typeset" (.-Hub js/MathJax) el])
                            (catch js/Error e (println (.-message e)))))}
   txt])

(defn mixed-comp [txt]
  (if (re-find #"(\\\(|\\\[)" txt)
    [tex-comp txt]
    [:pre txt]))

(defn error-comp []
  (let [flex50 {:style {:flex "50%"}}]
    [:div {:style {:display "flex"}}
     [:div flex50
      [:h3 "Error"]
      [:pre (:sci-error @state)]]
     [:div flex50
      [:h3 "Code"]
      [:pre (:code @state)]]]))

(defn result-comp []
  (if-let [ifo (ca/inspect-form (:edn-code @state) workspace!/inspect-fn-sym)]
    (let [msgs-fn (:messages (nth tutorials (:tutorial-no @state)))
          msg (msgs-fn ifo (:edn-code @state) (:sci-error @state))]
      [:<>
       (if (seq (:inspect @state))
         [:<>
          (map-indexed (fn [idx v]
                         ^{:key idx} [:<>
                                      [mixed-comp v]
                                      [:hr]])
                       (:inspect @state))
          (when msg [:p msg])]
         [:<>
          [mixed-comp (str "Evaluation error: " (last ifo))]
          (when msg [:p msg])
          (when (:sci-error @state) [error-comp])])])
    [:<>
     (map-indexed (fn [idx v]
                    ^{:key idx} [mixed-comp v])
                  (:stdout @state))
     (when (:sci-error @state)
       [error-comp])
     (if-let [last-vec (is-last-div)]
       [reagent-comp last-vec]
       [:pre (:result @state)])
     [:div {:style {:column-count 2}}
      [tex-comp (:desc @state)]]]))

(defn error-boundary [comp]
  (rc/create-class
    {:component-did-catch (fn [this e info] nil)
     :get-derived-state-from-error (fn [e]
                                     (swap! state assoc :reagent-error e)
                                     #js {})
     :reagent-render (fn [comp]
                       (if (:reagent-error @state)
                         [:pre "Something went wrong."]
                         comp))}))

(defn theview []
  [:div
   [modal-comp]
   [tutorials-comp]
   [error-boundary
    [result-comp]]])

(defn ^{:dev/after-load true} render []
  ;;((tutorial-fu identity))
  (rd/render [theview] (gdom/getElement "out")))

(defn ^{:export true} output []
  (workspace!/init startsci open-modal)
  (some-> (not-empty (.. js/window -location -search))
          js/URLSearchParams.
          (.get "page")
          js/parseInt
          dec
          goto-page!)
  ((tutorial-fu identity))
  (render))
