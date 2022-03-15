-- Before running drop any existing views
DROP VIEW IF EXISTS q0;
DROP VIEW IF EXISTS q1i;
DROP VIEW IF EXISTS q1ii;
DROP VIEW IF EXISTS q1iii;
DROP VIEW IF EXISTS q1iv;
DROP VIEW IF EXISTS q2i;
DROP VIEW IF EXISTS q2ii;
DROP VIEW IF EXISTS q2iii;
DROP VIEW IF EXISTS q3i;
DROP VIEW IF EXISTS q3ii;
DROP VIEW IF EXISTS q3iii;
DROP VIEW IF EXISTS q4i;
DROP VIEW IF EXISTS q4ii;
DROP VIEW IF EXISTS q4iii;
DROP VIEW IF EXISTS q4iv;
DROP VIEW IF EXISTS q4v;

-- Question 0
CREATE VIEW q0(era)
AS
 SELECT MAX(era)
 FROM pitching
;

-- Question 1i
CREATE VIEW q1i(namefirst, namelast, birthyear)
AS
  SELECT namefirst, namelast, birthyear 
  FROM people 
  WHERE weight > 300
;

-- Question 1ii
CREATE VIEW q1ii(namefirst, namelast, birthyear)
AS
  SELECT namefirst, namelast, birthyear
  FROM people
  WHERE namefirst LIKE '% %'
  ORDER BY namelast asc
;

-- Question 1iii
CREATE VIEW q1iii(birthyear, avgheight, count)
AS
  SELECT birthyear, AVG(height), COUNT(*)
  FROM people
  GROUP BY birthyear
  ORDER BY birthyear asc
;

-- Question 1iv
CREATE VIEW q1iv(birthyear, avgheight, count)
AS
  SELECT birthyear, AVG(height), COUNT(*)
  FROM people
  GROUP BY birthyear
  HAVING AVG(HEIGHT) > 70
  ORDER BY birthyear asc
;

-- Question 2i
CREATE VIEW q2i(namefirst, namelast, playerid, yearid)
AS
  SELECT p.namefirst, p.namelast, p.playerid, h.yearid 
  FROM people as p, HallofFame as h
  WHERE p.playerid = h.playerid AND h.inducted = 'Y'
  ORDER BY h.yearid desc, p.playerid asc
;

-- Question 2ii
CREATE VIEW q2ii(namefirst, namelast, playerid, schoolid, yearid)
AS
  SELECT q.namefirst, q.namelast, q.playerid, s.schoolid, q.yearid 
  FROM  q2i as q, collegeplaying as c, schools as s
  WHERE q.playerid = c.playerid AND c.schoolid = s.schoolid AND s.schoolstate = 'CA'
  ORDER BY q.yearid desc, s.schoolid asc, q.playerid asc
;

-- Question 2iii
CREATE VIEW q2iii(playerid, namefirst, namelast, schoolid)
AS
  SELECT q.playerid, q.namefirst, q.namelast, c.schoolid
  FROM  q2i as q LEFT OUTER JOIN collegeplaying as c 
  ON q.playerid = c.playerid 
  ORDER BY q.playerid desc, c.schoolid asc
;

-- Question 3i
-- Source for converting to float: https://www.w3schools.com/sql/func_sqlserver_cast.asp
CREATE VIEW q3i(playerid, namefirst, namelast, yearid, slg)
AS
  SELECT p.playerid, p.namefirst, p.namelast, b.yearid, CAST(1*b.H-b.H2B-b.H3B-b.HR + 2*b.H2B + 3*b.H3B + 4*b.HR AS float)/b.AB AS slg
  FROM People as p, Batting as b 
  WHERE p.playerid = b.playerid AND b.AB > 50
  ORDER BY slg desc, b.yearid asc, p.playerid asc
  LIMIT 10
;

-- Question 3ii
CREATE VIEW q3ii(playerid, namefirst, namelast, lslg)
AS
  SELECT p.playerid, p.namefirst, p.namelast, CAST(1*SUM(b.H-b.H2B-b.H3B-b.HR) + 2*SUM(b.H2B) + 3*SUM(b.H3B) + 4*SUM(b.HR) AS float)/CAST(SUM(b.AB) as float) AS lslg
  FROM People as p, Batting as b 
  WHERE p.playerid = b.playerid 
  GROUP BY b.playerid, p.namefirst, p.namelast
  HAVING SUM(b.AB) > 50
  ORDER BY lslg desc, p.playerid asc
  LIMIT 10
;

-- Question 3iii
CREATE VIEW q3iii(namefirst, namelast, lslg)
AS
  SELECT p.namefirst, p.namelast, CAST(1*SUM(b.H-b.H2B-b.H3B-b.HR) + 2*SUM(b.H2B) + 3*SUM(b.H3B) + 4*SUM(b.HR) AS float)/CAST(SUM(b.AB) as float) AS lslg
  FROM People as p, Batting as b 
  WHERE p.playerid = b.playerid 
  GROUP BY b.playerid, p.namefirst, p.namelast
  HAVING SUM(b.AB) > 50 AND lslg > (SELECT CAST(1*SUM(b.H-b.H2B-b.H3B-b.HR) + 2*SUM(b.H2B) + 3*SUM(b.H3B) + 4*SUM(b.HR) AS float)/CAST(SUM(b.AB) as float) AS lslg
  FROM People as p, Batting as b 
  WHERE p.playerid = b.playerid and b.playerid = 'mayswi01'
  GROUP BY b.playerid, p.namefirst, p.namelast)
  ORDER BY p.namefirst asc
;

-- Question 4i
CREATE VIEW q4i(yearid, min, max, avg)
AS
  SELECT s.yearid, min(s.salary), max(s.salary), AVG(s.salary) 
  FROM salaries as s
  GROUP BY s.yearid
  ORDER BY s.yearid asc
;

-- Question 4ii
-- Try thinking about the problem in steps
--   1. Find the maximum and minimum salary of all 2016
--   2. Find the high and low boundaries for each of the individual bins
--   3. Figure out which bin each salary falls under / Compute the number of salaries in each bin
CREATE VIEW q4ii(binid, low, high, count)
AS
  WITH SalaryRanges AS (
    SELECT min(s.salary) as minimum, max(s.salary) as maximum, CAST(max(s.salary)-min(s.salary) AS int)/10 as range2016
    FROM salaries as s
    WHERE s.yearid == '2016'
    ), binsHistogram AS (
      SELECT b.binid as binid, (minimum+SR.range2016*b.binid) as low, (minimum+SR.range2016*(b.binid+1)) as high
      FROM SalaryRanges as SR, binids as b
    ), findCount AS (
      SELECT b.binid as binid, COUNT(CASE WHEN s.salary >= b.low AND s.salary <= b.high THEN 1 END) as count
      FROM binsHistogram as b,salaries as s
      WHERE s.yearid == '2016'
      GROUP BY binid
    )
    SELECT bh.binid,bh.low,bh.high,fc.count
    FROM binsHistogram as bh INNER JOIN findCount as fc
    ON bh.binid == fc.binid
;

-- Question 4iii
-- @source for histogram idea: https://stackoverflow.com/questions/485409/generating-a-histogram-from-column-values-in-a-database
CREATE VIEW q4iii(yearid, mindiff, maxdiff, avgdiff)
AS 
  SELECT prev.yearid, prev.small-i.min, prev.large-i.max, prev.average-i.avg
  FROM q4i as i INNER JOIN (SELECT sp.yearid, min(sp.salary) as small, max(sp.salary) as large, AVG(sp.salary) as average
  FROM salaries as sp
  GROUP BY sp.yearid
  ORDER BY sp.yearid asc) as prev
  ON i.yearid = prev.yearid - 1
  ORDER BY i.yearid asc
;

-- Question 4iv
-- @Source for in: https://stackoverflow.com/questions/1136380/sql-where-in-clause-multiple-columns/1136381
CREATE VIEW q4iv(playerid, namefirst, namelast, salary, yearid)
AS
  SELECT p.playerid, p.namefirst, p.namelast, s.salary, s.yearid
  FROM salaries as s INNER JOIN people as p
  ON s.playerid = p.playerid
  WHERE (s.yearid < 2002 AND s.yearid > 1999) AND (s.yearid,s.salary) IN (SELECT s.yearid,MAX(salary)
  FROM salaries as s
  WHERE s.yearid < 2002 AND s.yearid > 1999
  GROUP BY s.yearid) 
;

-- Question 4v
CREATE VIEW q4v(team, diffAvg) AS
  SELECT a.teamID, (MAX(s.salary) - MIN(s.salary)) as diffAvg
  FROM allstarfull as a INNER JOIN salaries as s
  ON a.playerid == s.playerid 
  WHERE a.yearID == '2016' AND s.yearid == '2016'
  GROUP BY a.teamID
;

