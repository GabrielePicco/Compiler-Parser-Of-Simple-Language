integer i, k, m, n, res;
begin
   k := 10;
   if k == 0 then res := 0
   else begin
      i := 1;
      m := 0;
      n := 1;
      while i <= k - 1 do begin
	 n := n + m;
	 m := n - m;
	 i := i + 1
      end;
      res := n
   end;
   print(res)
end