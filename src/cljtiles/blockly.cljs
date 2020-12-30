(ns cljtiles.blockly
  (:require
   ["blockly" :as blockly]

   [goog.dom :as gdom]
   ))

(def dev true)
(if dev
  (def menu false)
  (def menu false))

(defonce workspace
  (let [workspace-item
        #js {:displayText "Hello World"
             :preconditionFn (fn [scope] (println "klm uu") "enabled")
             :callback (fn [scope] (println "klm called" scope))
             :scopeType (.. blockly -ContextMenuRegistry -ScopeType -WORKSPACE)
             :id "hello_world"
             :weight 100}
        block-item
        #js {:displayText "Hello World Block"
             :preconditionFn (fn [scope] (println "klm uu") "enabled")
             :callback (fn [scope] (.log js/console scope))
             :scopeType (.. blockly -ContextMenuRegistry -ScopeType -BLOCK)
             :id "hello_world_blcok"
             :weight 100}]
    (do
      (js/initblocks blockly)
      (.inject blockly
               "blocklyDiv"
               (clj->js (merge {:scrollbars true
                                :media "/blockly/media/"}
                               (when menu {:toolbox (gdom/getElement "toolbox")}))))
      (.. blockly -ContextMenuRegistry -registry (register workspace-item))
      (.. blockly -ContextMenuRegistry -registry (register block-item))
      )))
