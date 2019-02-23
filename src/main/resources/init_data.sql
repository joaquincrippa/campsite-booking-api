SET @CAPACITY = 10;
WITH RECURSIVE T(N) AS (
    SELECT 1, GETDATE() AS DATE, @CAPACITY AS VALUE
    UNION ALL
    SELECT N+1, DATEADD('DAY',N,GETDATE()), @CAPACITY FROM T WHERE N<370
)
INSERT INTO AVAILABILITY(DATE, VALUE)
SELECT DATE, VALUE FROM T;