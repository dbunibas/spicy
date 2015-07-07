<Target>
  {
   for $x0 in /Source/Gene
   where $x0/type/text() = "primary"
   return
   <Gene>
      <Name>{ $x0/name/text()}</Name>
      <Protein>{$x0/protein/text()}</Protein>
   </Gene>
   }
   {
   for $x0 in /Source/Gene
   where $x0/type/text() != "primary"
   return
   <Synonym>
      <Name>{$x0/name/text()}</Name>
      <Protein>{$x0/protein/text()}</Protein>
   </Synonym>
   }
</Target>