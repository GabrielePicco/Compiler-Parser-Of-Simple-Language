integer x,y;
boolean ris;
begin
  ris := false;
  x := 2;
  y := 3;
  if x > y
  then
    begin
      ris := 2 > 3;
      print(ris)
    end
  else
    begin
      ris := ((x == 3) && (30/1 > 3));
      print(ris);
      print(y)
    end;
  print(x)
end
