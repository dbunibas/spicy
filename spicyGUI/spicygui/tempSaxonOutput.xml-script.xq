
declare function local:removeDuplicateTuples($set as element(set)) as element(set) {
   <set> {
      let $tuple := $set/tuple
      for $tupleId in fn:distinct-values($tuple/tupleId)
      return $tuple[tupleId/text()=$tupleId][1]
   } </set>
};

declare function local:skolem($name as xs:string, $args as xs:string*) as xs:string {
   fn:concat($name, "(", fn:string-join($args, ""), ")")
};

declare function local:newId() as xs:string {
   (: plug-in your id generation strategy :)
   "newId()"
};

  let $doc:=fn:doc(""),


(:-----------------------  SOURCE TO TARGET EXCHANGE -------------------------------:)

      $VIEW_v1 := element set {
            for 
            $v1 in $doc/Source/Name
      
            return
                  element tuple {
                        element v1_id {$v1/id/text()},
                        element v1_name {$v1/name/text()},
                        element v1_uniqueName {$v1/uniqueName/text()},
                        element v1_class {$v1/class/text()}
                  }
      },

      $VIEW_v2v1 := element set {
            for 
            $v2 in $doc/Source/Node,
            $v1 in $doc/Source/Name
            where $v2/emblCode/text() = $v1/id/text()
            return
                  element tuple {
                        element v2_taxId {$v2/taxId/text()},
                        element v2_parentId {$v2/parentId/text()},
                        element v2_rank {$v2/rank/text()},
                        element v2_emblCode {$v2/emblCode/text()},
                        element v1_id {$v1/id/text()},
                        element v1_name {$v1/name/text()},
                        element v1_uniqueName {$v1/uniqueName/text()},
                        element v1_class {$v1/class/text()}
                  }
      },


(:------------------------------ APPLY FUNCTION VIEWS -----------------------------------:)


(:------------------------------  TGD  -----------------------------------:)

      $TGD_v1_v0_AF := element set {
            for $variable in $VIEW_v1/tuple
            return (
                  element tuple {
                        element v0_Id { $variable/v1_id/text() },
                        element v0_Name { $variable/v1_name/text() },
                        element v0_UniqueName { $variable/v1_uniqueName/text() },
                        element v0_Class { $variable/v1_class/text() }
                  }
            )
      }      ,

(:------------------------------  TGD  -----------------------------------:)

      $TGD_v2v1_v0_AF := element set {
            for $variable in $VIEW_v2v1/tuple
            return (
                  element tuple {
                        element v0_Id { $variable/v1_id/text() },
                        element v0_Name { $variable/v1_name/text() },
                        element v0_UniqueName { $variable/v1_uniqueName/text() },
                        element v0_Class { $variable/v1_class/text() },
                        element v0_Parent { $variable/v2_parentId/text() },
                        element v0_Rank { $variable/v2_rank/text() },
                        element v0_EmblCode { $variable/v2_emblCode/text() }
                  }
            )
      }      ,

(:------------------------------ COVERAGE VIEWS -----------------------------------:)


(:------------------------------ TGDS -----------------------------------:)

      $TGD_v1_v0 := element set {
            for $variable in $TGD_v1_v0_AF/tuple
            return (
                  element tuple {
                        element v0_Id {$variable/v0_Id/text()},
                        element v0_Name {$variable/v0_Name/text()},
                        element v0_UniqueName {$variable/v0_UniqueName/text()},
                        element v0_Class {$variable/v0_Class/text()},
                        element v0_Parent {"NULL"},
                        element v0_Rank {"NULL"},
                        element v0_EmblCode {"NULL"} 
                  }
            )
      },
      $TGD_v2v1_v0 := element set {
            for $variable in $TGD_v2v1_v0_AF/tuple
            return (
                  element tuple {
                        element v0_Id {$variable/v0_Id/text()},
                        element v0_Name {$variable/v0_Name/text()},
                        element v0_UniqueName {$variable/v0_UniqueName/text()},
                        element v0_Class {$variable/v0_Class/text()},
                        element v0_Parent {$variable/v0_Parent/text()},
                        element v0_Rank {$variable/v0_Rank/text()},
                        element v0_EmblCode {$variable/v0_EmblCode/text()} 
                  }
            )
      },
      $TGD_v1_v0_DIFF := element set {
            for $variable in $TGD_v1_v0/tuple
            let $join_difference0 := $TGD_v2v1_v0_AF/tuple[./v0_Id = $variable/v0_Id and ./v0_Name = $variable/v0_Name and ./v0_UniqueName = $variable/v0_UniqueName and ./v0_Class = $variable/v0_Class]
            return (
            if (empty($join_difference0))
                  then 
                  element tuple 
                        { $variable/* }
            else ()
            )
      },


(:-----------------------  RESULT OF S-T EXCHANGE ---------------------------:)

      $ST_EXCHANGE_RESULT_FOR_v0_Target := local:removeDuplicateTuples(element set {
      for $variable in $TGD_v1_v0_DIFF/tuple
      return (
            element tuple {
                        element tupleId {fn:concat(local:skolem("Taxon", ("SK{T=[0.0:",$variable/v0_Id/text(),",0.1:",$variable/v0_Name/text(),",0.2:",$variable/v0_UniqueName/text(),",0.3:",$variable/v0_Class/text(),"]}")), " in ", local:skolem("Target", ()))},
                        element setId {local:skolem("Target", ())},
                        element v0_Id {$variable/v0_Id/text()},
                        element v0_Name {$variable/v0_Name/text()},
                        element v0_UniqueName {$variable/v0_UniqueName/text()},
                        element v0_Class {$variable/v0_Class/text()},
                        element v0_Parent {$variable/v0_Parent/text()},
                        element v0_Rank {$variable/v0_Rank/text()},
                        element v0_EmblCode {$variable/v0_EmblCode/text()}
            }
      ),
      for $variable in $TGD_v2v1_v0/tuple
      return (
            element tuple {
                        element tupleId {fn:concat(local:skolem("Taxon", ("SK{T=[0.0:",$variable/v0_Id/text(),",0.1:",$variable/v0_Name/text(),",0.2:",$variable/v0_UniqueName/text(),",0.3:",$variable/v0_Class/text(),",0.4:",$variable/v0_Parent/text(),",0.5:",$variable/v0_Rank/text(),",0.6:",$variable/v0_EmblCode/text(),"]}")), " in ", local:skolem("Target", ()))},
                        element setId {local:skolem("Target", ())},
                        element v0_Id {$variable/v0_Id/text()},
                        element v0_Name {$variable/v0_Name/text()},
                        element v0_UniqueName {$variable/v0_UniqueName/text()},
                        element v0_Class {$variable/v0_Class/text()},
                        element v0_Parent {$variable/v0_Parent/text()},
                        element v0_Rank {$variable/v0_Rank/text()},
                        element v0_EmblCode {$variable/v0_EmblCode/text()}
            }
      )
      })
return 

element Target {
    for $v0 in $ST_EXCHANGE_RESULT_FOR_v0_Target/tuple
    return (
        element Taxon {
            element Id {$v0/v0_Id/text()},
            element Name {$v0/v0_Name/text()},
            element UniqueName {$v0/v0_UniqueName/text()},
            element Class {$v0/v0_Class/text()},
            element Parent {$v0/v0_Parent/text()},
            element Rank {$v0/v0_Rank/text()},
            element EmblCode {$v0/v0_EmblCode/text()}
        }
    )
}