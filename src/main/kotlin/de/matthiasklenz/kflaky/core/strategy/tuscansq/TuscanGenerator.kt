package de.matthiasklenz.kflaky.core.strategy.tuscansq

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

class TuscanGenerator {
    fun generate(n: Int): TuscanCalulation {
        return generateTuscanSquare(n)
    }

    fun generate(): File {
        val calculations = mutableListOf<TuscanCalulation>()
        val min = 2
        val max = 100
        for (i in min..max) {
            calculations.add(generateTuscanSquare(i))
        }

        val jsonFile = File(
            Thread.currentThread().contextClassLoader?.getResource("tuscan_calculations.json")?.path
                ?: throw FileNotFoundException("File not found!")
        )
        if (!jsonFile.exists()) {
            jsonFile.createNewFile()
        }
        val obj = AllTuscanCalculations(min, max, calculations)
        jsonFile.writeText(Json.encodeToString(obj))

        return jsonFile
    }

    private var r: Array<IntArray?> = arrayOfNulls(0)
    private fun helper(a: IntArray, i: Int) {
        System.arraycopy(a, 0, r[i], 0, a.size)
    }

    private fun generateTuscanSquare(n: Int): TuscanCalulation {
        if (n == 3) {
            return TuscanCalulation(
                size = 3,
                listOf(
                    listOf(1, 2, 3),
                    listOf(3, 2, 1)
                )
            )
        }
        if (n == 5) {
            return TuscanCalulation(
                size = 5,
                listOf(
                    listOf(1, 2, 3, 4, 5),
                    listOf(2, 1, 4, 3, 5),
                    listOf(5, 3, 2, 4, 1),
                    listOf(3, 1, 4, 5, 2),
                    listOf(4, 2, 5, 1, 3)
                )
            )
        }

        var n = n
        val nn = n
        while ((n - 1) % 4 == 0 && n != 1 && n != 9) n = (n - 1) / 2 + 1

        r = arrayOfNulls(nn)
        for (i in 0..<nn) {
            r[i] = IntArray(nn + 1)
        }

        if (n % 2 == 0) {
            // https://mathoverflow.net/questions/60856/hamilton-paths-in-k-2n/60859#60859
            val a = IntArray(n)
            var i = 0
            while (i < n) {
                a[i] = i / 2
                a[i + 1] = n - 1 - a[i]
                i += 2
            }
            helper(a, 0)
            for (j in 1..<n) {
                for (i in 0..<n) {
                    a[i] = (a[i] + 1) % n
                }
                helper(a, j)
            }
        } else if (n % 4 == 3) {
            val k = (n - 3) / 4
            val b = IntArray(n)
            for (i in 0..<n - 1) {
                val p = when(i) {
                    (0) -> 1
                    (k + 1) -> 4 * k + 2
                    (2 * k + 2) -> 3
                    (3 * k + 2) -> 4 * k
                    else -> 2 * k
                }
                val a = IntArray(n)
                for (j in 0..<n) {
                    val index = if (j < p) n + j - p else j - p
                    a[index] =
                        if (j == 0) (n - 1) else (i + (if (j % 2 == 0) (j / 2) else (n - 1 - (j - 1) / 2))) % (n - 1)
                }
                b[a[n - 1]] = a[0]
                helper(a, i)
            }
            val t = IntArray(n)
            t[0] = n - 1
            for (i in 1..<n) {
                t[i] = b[t[i - 1]]
            }
            helper(t, n - 1)
        } else if (n == 9) {
            val t = arrayOf(
                intArrayOf(0, 1, 7, 2, 6, 3, 5, 4, 8),
                intArrayOf(3, 7, 4, 6, 5, 8, 1, 2, 0),
                intArrayOf(1, 4, 0, 5, 7, 6, 8, 2, 3),
                intArrayOf(6, 0, 7, 8, 3, 4, 2, 5, 1),
                intArrayOf(2, 7, 1, 0, 8, 4, 5, 3, 6),
                intArrayOf(7, 3, 0, 2, 1, 8, 5, 6, 4),
                intArrayOf(5, 0, 4, 1, 3, 2, 8, 6, 7),
                intArrayOf(4, 3, 8, 7, 0, 6, 1, 5, 2),
                intArrayOf(8, 0, 3, 1, 6, 2, 4, 7, 5)
            )
            for (i in 0..8) {
                helper(t[i], i)
            }
        } else assert(false)

        while (nn != n) {
            // n + 1 == 4*m - 2
            // https://www.sciencedirect.com/science/article/pii/0095895680900441

            n = n * 2 - 1

            val h = (n + 1) / 2

            for (i in 0..<h) {
                for (j in 0..<h) {
                    r[i]!![n - j] = r[i]!![j] + h
                }
                // System.out.println(java.util.Arrays.toString(r[i]));
            }
            for (i in h..<n) {
                /*
                for (int j = 0; j < n+1; j++) {
                    r[i][j] = ((j % 2 == 0) ? 0 : h) + (i-h + ((j % 2 == 0) ? (j / 2) : (n - (j - 1) / 2))) % h;
                }
                */
                for (j in 0..<h - 1) {
                    r[i]!![j] =
                        (if (j % 2 == 0) 0 else h) + (i - h + (if (j % 2 == 0) (j / 2) else (h - 2 - (j - 1) / 2))) % (h - 1)
                }
                r[i]!![h - 1] = h - 1
                for (j in h..<n + 1) {
                    r[i]!![j] = (if (j % 2 == 0) 0 else h) + r[i]!![j - h] % h
                }
                // System.out.println(java.util.Arrays.toString(r[i]));
            }
            for (i in 0..<n) {
                var l = 0
                while (l < n) {
                    if (r[i]!![l] == n) break
                    l++
                }
                val t = IntArray(n)
                System.arraycopy(r[i], l + 1, t, 0, n - l)
                System.arraycopy(r[i], 0, t, n - l, l)

                System.arraycopy(t, 0, r[i], 0, n)
            }
        }

        val matrix = mutableListOf<List<Int>>()

        for (i in 0..<nn) {
            val current = mutableListOf<Int>()
            for (j in 0..<nn) {
                current.add(r[i]!![j])
            }
            matrix.add(current)
        }

        return TuscanCalulation(n, matrix)
    }
}