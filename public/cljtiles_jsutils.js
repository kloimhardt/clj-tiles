	function LCSubStr(X,Y)
	{
		// Find length of both the strings.
	let m = X.length;
	let n = Y.length;

	// Variable to store length of longest
	// common substring.
	let result = 0;

	// Variable to store ending point of
	// longest common substring in X.
	let end;

	// Matrix to store result of two
	// consecutive rows at a time.
	let len= new Array(2);
	for(let i=0;i<len.length;i++)
	{
		len[i]=new Array(n);
		for(let j=0;j<n;j++)
		{
			len[i][j]=0;
		}
	}

	// Variable to represent which row of
	// matrix is current row.
	let currRow = 0;

	// For a particular value of i and j,
	// len[currRow][j] stores length of longest
	// common substring in string X[0..i] and Y[0..j].
	for (let i = 0; i <= m; i++) {
		for (let j = 0; j <= n; j++) {
			if (i == 0 || j == 0) {
				len[currRow][j] = 0;
			}
			else if (X[i - 1] == Y[j - 1]) {
				len[currRow][j] = len[1 - currRow][j - 1] + 1;
				if (len[currRow][j] > result) {
					result = len[currRow][j];
					end = i - 1;
				}
			}
			else {
				len[currRow][j] = 0;
			}
		}

		// Make current row as previous row and
		// previous row as new current row.
		currRow = 1 - currRow;
	}

	// If there is no common substring, print -1.
	if (result == 0) {
		return "-1";
	}

	// Longest common substring is from index
	// end - result + 1 to index end in X.
	return X.substr(end - result + 1, result);
	}
