package com.example.hjd.aquasift.Misc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hjd.aquasift.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.hjd.aquasift.Misc.DbHelper.packGraphData;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;

/**
 * Created by HJD on 12/30/2016.
 */

public class DisplayResultsTask extends AsyncTask<Void, DataPoint, Void> {

    UsbHelper usbHelper;
    Activity activity;
    Context context;
    ArrayList<ArrayList<Integer>> dataList;

    private int reverse;

    private int startVoltage;
    private int endVoltage;

    private int gainResistor;

    private ArrayList<ArrayList<Pair<Float, Float>>> currentList;
    private ArrayList<ArrayList<Pair<Float, Float>>> smoothedCurrentList;
    private ArrayList<ArrayList<DataPoint>> graphedData;

    private ProgressDialog progressDialog;

    private GraphView graph;

    private Long time;

    public DisplayResultsTask(UsbHelper usbHelper, Activity activity,
                              Context context, ArrayList<ArrayList<Integer>> dataList, ProgressDialog progressDialog) {
        this.usbHelper = usbHelper;
        this.activity = activity;
        this.context = context;
        this.dataList = dataList;
        this.progressDialog = progressDialog;

        //For testing purposes
        this.dataList = new ArrayList<>();
        ArrayList<Integer> s1= new ArrayList<>(Arrays.asList(1105,1267,1356,1417,1465,1500,1527,1553,1575,1592,1605,1620,1633,1647,1656,1664,1674,1683,1690,1695,1703,1712,1716,1719,1725,1733,1736,1739,1745,1751,1755,1756,1761,1766,1769,1770,1774,1780,1782,1782,1787,1792,1795,1796,1796,1799,1805,1806,1806,1809,1815,1815,1816,1818,1823,1823,1822,1826,1831,1831,1831,1833,1838,1838,1838,1841,1844,1845,1846,1847,1852,1853,1853,1852,1855,1859,1859,1859,1861,1865,1865,1863,1867,1871,1870,1869,1872,1876,1876,1874,1877,1882,1881,1880,1882,1885,1885,1884,1886,1890,1893,1891,1890,1892,1895,1895,1894,1897,1900,1900,1898,1901,1904,1903,1902,1904,1908,1907,1905,1909,1912,1911,1909,1912,1915,1915,1912,1914,1918,1917,1918,1916,1919,1923,1921,1920,1921,1924,1923,1923,1924,1927,1926,1925,1926,1929,1929,1927,1929,1931,1932,1930,1931,1935,1933,1932,1934,1937,1935,1936,1935,1937,1940,1939,1938,1939,1942,1941,1939,1941,1944,1943,1942,1943,1946,1945,1944,1945,1948,1947,1945,1947,1949,1949,1948,1950,1953,1951,1953,1950,1953,1956,1955,1953,1954,1958,1956,1955,1956,1959,1958,1956,1958,1962,1960,1957,1960,1963,1961,1959,1961,1963,1963,1961,1963,1965,1964,1960,1962,1965,1968,1966,1965,1966,1969,1968,1966,1967,1970,1969,1967,1969,1971,1970,1968,1970,1973,1971,1970,1971,1973,1972,1970,1972,1974,1974,1969,1971,1973,1976,1974,1972,1974,1976,1975,1974,1975,1978,1977,1975,1976,1979,1978,1976,1977,1979,1978,1977,1979,1981,1979,1978,1979,1981,1980,1976,1978,1980,1983,1982,1980,1981,1984,1983,1981,1983,1985,1984,1982,1983,1986,1985,1983,1985,1988,1985,1984,1985,1988,1987,1984,1986,1989,1988,1986,1982,1987,1990,1988,1986,1988,1991,1990,1987,1989,1992,1990,1989,1989,1992,1991,1989,1990,1993,1992,1990,1991,1995,1993,1991,1993,1994,1993,1992,1989,1992,1996,1995,1992,1994,1996,1996,1993,1994,1996,1995,1993,1995,1998,1996,1993,1995,1997,1997,1995,1996,1999,1998,1996,1998,1999,1998,1997,1993,1998,2000,1999,1997,1998,2001,1999,1998,1999,2001,2001,1999,2001,2003,2002,2000,2002,2004,2003,2000,2002,2004,2003,2002,2003,2005,2004,2002,2000,2005,2009,2007,2004,2006,2009,2008,2006,2008,2010,2009,2006,2008,2012,2011,2008,2011,2013,2012,2010,2011,2013,2014,2012,2014,2017,2017,2015,2016,2016,2019,2019,2018,2019,2021,2022,2020,2021,2026,2024,2023,2025,2028,2027,2026,2028,2032,2032,2029,2033,2036,2035,2033,2036,2039,2040,2038,2040,2039,2044,2044,2044,2045,2049,2051,2049,2052,2055,2056,2055,2057,2062,2063,2062,2064,2068,2070,2069,2071,2076,2078,2078,2081,2084,2086,2086,2089,2089,2095,2097,2097,2100,2106,2108,2108,2111,2117,2119,2119,2123,2128,2132,2133,2139,2146,2149,2150,2154,2161,2164,2164,2170,2177,2181,2183,2188,2195,2197,2200,2203,2208,2217,2222,2224,2229,2237,2242,2244,2250,2258,2262,2266,2271,2280,2284,2287,2293,2301,2307,2309,2316,2325,2331,2334,2339,2348,2351,2357,2361,2366,2376,2383,2386,2392,2403,2406,2408,2412,2419,2424,2426,2431,2439,2443,2445,2449,2457,2463,2465,2470,2476,2480,2483,2487,2493,2496,2499,2500,2504,2510,2515,2516,2519,2526,2529,2529,2532,2538,2540,2539,2542,2546,2548,2547,2548,2552,2552,2549,2550,2552,2550,2548,2546,2548,2545,2539,2536,2535,2535,2531,2523,2518,2514,2506,2495,2485,2476,2462,2443,2427,2412,2392,2368,2347,2328,2306,2283,2265,2251,2235,2219,2208,2201,2191,2181,2171,2165,2163,2158,2150,2147,2147,2142,2136,2134,2135,2131,2127,2125,2126,2124,2119,2118,2121,2119,2115,2114,2117,2113,2109,2108,2111,2108,2107,2104,2104,2106,2104,2100,2101,2104,2103,2100,2099,2102,2101,2097,2098,2100,2099,2096,2096,2097,2097,2094,2095,2098,2096,2093,2094,2096,2095,2089,2092,2091,2094,2094,2090,2091,2094,2093,2090,2091,2094,2093,2090,2090,2093,2092,2091,2092,2094,2093,2091,2091,2095,2093,2091,2091,2094,2094,2087,2089,2091,2093,2092,2090,2090,2094,2093,2089,2090,2092,2093,2089,2090,2093,2092,2089,2089,2092,2091,2088,2089,2092,2091,2089,2089,2092,2091,2086,2088,2089,2092,2090,2088,2089,2092,2091,2088,2088,2091,2089,2086,2087,2089,2089,2087,2087,2090,2088,2086,2086,2088,2088,2085,2087,2089,2087,2084,2079,2084,2088,2087,2084,2085,2087,2087,2084,2085,2087,2086,2083,2085,2088,2086,2083,2085,2088,2087,2085,2084,2087,2088,2084,2085,2087,2086,2084,2079,2084,2087,2088,2084,2086,2089,2089,2085,2087,2090,2089,2085,2088,2091,2089,2086,2088,2090,2089,2087,2088,2092,2089,2087,2088,2090,2091,2087,2082,2087,2091,2089,2087,2088,2091,2090,2087,2088,2090,2090,2088,2088,2091,2090,2087,2088,2092,2091,2087,2088,2091,2090,2088,2089,2090,2090,2086,2087,2086,2089,2088,2085,2087,2090,2088,2086,2086,2089,2087,2085,2085,2088,2088,2085,2086,2088,2087,2085,2085,2089,2087,2084,2086,2087,2086,2086,2086,2083,2087,2087,2085,2085,2087,2087,2084,2085,2088,2087,2085,2084,2087,2086,2084,2085,2088,2089,2085,2086,2089,2089,2086,2087,2089,2088,2086,2087,2086,2089,2090,2086,2086,2090,2088,2085,2087,2090,2089,2087,2087,2090,2090,2087,2087,2090,2089,2086,2089,2091,2090,2088,2088,2091,2089,2087,2088,2092,2092,2091,2088,2088,2091,2091,2088,2090,2092,2091,2089,2089,2090,2091,2088,2088,2091,2090,2088,2089,2091,2090,2088,2090,2092,2091,2089,2091,2092,2092,2091,2089,2090,2093,2091,2089,2091,2093,2093,2089,2092,2094,2093,2092,2092,2094));

        ArrayList<Integer> s2 = new ArrayList<>(Arrays.asList(2081,2069,2066,2063,2058,2057,2058,2055,2051,2051,2053,2051,2048,2048,2049,2047,2044,2044,2045,2046,2044,2041,2042,2045,2043,2039,2040,2042,2040,2038,2038,2041,2039,2037,2038,2039,2038,2034,2034,2037,2036,2032,2034,2036,2034,2031,2032,2033,2034,2034,2030,2032,2035,2032,2030,2030,2033,2032,2029,2031,2032,2031,2029,2030,2031,2030,2027,2029,2032,2030,2027,2028,2030,2029,2026,2028,2028,2031,2029,2026,2026,2030,2029,2025,2026,2030,2028,2024,2027,2029,2029,2025,2027,2029,2028,2026,2027,2028,2027,2024,2026,2028,2026,2025,2026,2026,2027,2027,2024,2025,2027,2026,2024,2025,2028,2026,2023,2024,2027,2026,2022,2024,2026,2025,2023,2023,2026,2025,2023,2023,2026,2024,2022,2023,2027,2025,2024,2021,2022,2024,2023,2020,2021,2023,2021,2018,2019,2022,2021,2018,2018,2021,2020,2017,2017,2020,2020,2017,2018,2021,2019,2016,2019,2023,2021,2019,2016,2016,2018,2018,2015,2016,2019,2017,2014,2015,2018,2016,2014,2015,2016,2016,2013,2014,2017,2015,2012,2014,2017,2016,2010,2014,2019,2017,2017,2014,2015,2017,2017,2014,2014,2016,2015,2013,2013,2016,2016,2013,2014,2016,2015,2012,2014,2016,2015,2013,2013,2016,2014,2011,2012,2014,2020,2015,2012,2013,2015,2014,2012,2013,2014,2013,2011,2012,2014,2013,2011,2012,2013,2013,2011,2012,2012,2012,2008,2009,2012,2011,2008,2009,2011,2016,2012,2008,2009,2011,2010,2007,2008,2011,2010,2006,2008,2011,2010,2006,2008,2010,2009,2006,2008,2009,2009,2007,2007,2009,2008,2006,2006,2010,2014,2009,2006,2007,2009,2008,2005,2005,2008,2007,2005,2006,2007,2006,2005,2005,2009,2009,2006,2006,2009,2008,2007,2006,2009,2007,2005,2006,2008,2008,2008,2005,2006,2009,2007,2004,2006,2008,2007,2005,2006,2008,2007,2005,2005,2008,2006,2004,2004,2007,2007,2003,2005,2007,2006,2003,2005,2007,2005,2007,2003,2004,2007,2005,2004,2003,2004,2003,2001,2000,2004,2003,2000,2001,2003,2002,2000,2000,2003,2001,1998,2000,2001,2000,1998,1998,2001,2000,2002,1999,1998,2001,2000,1997,1997,1999,1999,1996,1996,2000,1999,1996,1997,1999,1998,1994,1996,1998,1997,1994,1995,1998,1997,1994,1994,1996,1995,1998,1995,1996,1998,1997,1995,1996,1999,1997,1995,1995,1997,1996,1994,1995,1997,1995,1993,1993,1996,1995,1993,1993,1995,1994,1992,1992,1994,1993,1990,1990,1990,1993,1992,1988,1988,1990,1989,1986,1986,1988,1987,1983,1983,1985,1983,1980,1980,1982,1979,1976,1974,1975,1973,1967,1968,1968,1966,1960,1960,1959,1960,1957,1952,1950,1951,1948,1943,1941,1941,1938,1933,1931,1931,1928,1924,1921,1921,1918,1912,1910,1910,1907,1902,1900,1900,1897,1891,1891,1890,1889,1886,1880,1878,1879,1876,1872,1872,1870,1867,1863,1862,1863,1860,1857,1856,1858,1857,1853,1853,1854,1854,1851,1851,1853,1852,1849,1849,1850,1851,1850,1848,1848,1850,1850,1848,1847,1850,1850,1847,1848,1851,1851,1849,1850,1853,1852,1850,1852,1854,1855,1852,1855,1857,1857,1855,1857,1858,1862,1861,1860,1862,1865,1864,1861,1864,1867,1867,1865,1867,1871,1870,1869,1871,1874,1874,1872,1874,1877,1877,1875,1878,1881,1880,1879,1883,1883,1886,1886,1884,1885,1889,1889,1887,1889,1892,1892,1890,1892,1895,1896,1893,1896,1898,1898,1896,1898,1901,1901,1899,1901,1904,1903,1902,1904,1909,1908,1907,1905,1907,1911,1909,1908,1910,1913,1912,1910,1913,1915,1914,1911,1914,1918,1916,1914,1917,1920,1918,1916,1918,1921,1920,1918,1920,1925,1924,1922,1921,1922,1925,1924,1923,1924,1927,1926,1924,1925,1928,1928,1925,1927,1930,1928,1927,1928,1931,1930,1927,1929,1931,1931,1928,1929,1935,1934,1932,1930,1932,1934,1934,1931,1932,1934,1934,1931,1933,1935,1935,1932,1935,1937,1936,1934,1936,1938,1936,1935,1936,1940,1937,1936,1937,1939,1944,1939,1938,1939,1941,1939,1937,1939,1942,1940,1939,1940,1942,1941,1939,1940,1943,1942,1941,1942,1944,1943,1942,1942,1945,1944,1942,1943,1945,1949,1945,1943,1944,1947,1945,1943,1945,1947,1946,1944,1945,1947,1947,1945,1946,1948,1948,1945,1946,1949,1948,1945,1947,1949,1948,1946,1947,1949,1952,1949,1946,1948,1950,1949,1947,1948,1950,1948,1946,1948,1951,1949,1947,1948,1951,1949,1946,1948,1950,1948,1947,1948,1951,1949,1947,1948,1950,1949,1950,1948,1948,1950,1949,1946,1948,1950,1949,1946,1948,1950,1947,1946,1947,1950,1948,1945,1947,1948,1947,1944,1945,1949,1947,1944,1945,1947,1946,1946,1943,1945,1947,1946,1944,1945,1947,1946,1944,1944,1947,1946,1943,1944,1946,1945,1942,1943,1946,1944,1943,1943,1945,1943,1942,1943,1945,1943,1944,1941,1942,1944,1943,1940,1942,1944,1942,1940,1941,1943,1942,1939,1940,1942,1941,1938,1940,1942,1941,1938,1939,1941,1938,1936,1937,1939,1937,1936,1935,1936,1938,1937,1934,1935,1936,1936,1933,1933,1936,1935,1931,1933,1935,1933,1930,1930,1933,1931,1928,1929,1932,1929,1926,1927,1929,1927,1925,1924,1925,1927,1925,1922,1923,1924,1923,1919,1920,1921,1920,1916,1917,1919,1917,1913,1914,1916,1914,1911,1911,1913,1911,1908,1908,1910,1907,1904,1902,1904,1905,1903,1899,1900,1901,1899,1896,1896,1897,1894,1891,1891,1893,1890,1887,1887,1888,1885,1882,1881,1882,1880,1876,1876,1876,1874,1871,1871,1869,1870,1867,1864,1863,1863,1861,1856,1856,1856,1853,1849,1849,1849,1845,1841,1840,1840,1837,1833,1832,1831,1828,1823,1823,1822,1818,1814,1813,1811,1810,1807,1802,1800,1800,1796,1791,1789,1789,1785,1780,1779,1778,1774,1768,1766,1778,1782));

        this.dataList.add(s1);
        this.dataList.add(s2);
    }

    @Override
    protected void onPreExecute() {
        startVoltage = usbHelper.getSweepStartVoltage();
        endVoltage = usbHelper.getSweepEndVoltage();

        gainResistor = usbHelper.getGainResistor();

        graph = (GraphView) activity.findViewById(R.id.graph);

        currentList = new ArrayList<>();
        smoothedCurrentList = new ArrayList<>();
        graphedData = new ArrayList<>();

        if (startVoltage > endVoltage) {
            reverse = 1;
        } else {
            reverse = 0;
        }

        progressDialog.setMessage("Smoothing and Graphing Data");
    }

    @Override
    protected Void doInBackground(Void... params) {
        float currentVoltage;
        int voltageDiff = abs(startVoltage-endVoltage);
        float voltageIncrement;


        for (int i=0; i < dataList.size(); i++) {
            ArrayList<Integer> activeDataList = dataList.get(i);
            ArrayList<Pair<Float,Float>> activeCurrentList = new ArrayList<>();
            ArrayList<Pair<Float, Float>> activeSmoothedCurrentList = new ArrayList<>();

            currentVoltage = startVoltage;

            voltageIncrement = (float)voltageDiff / activeDataList.size();
            //int numDataPoints = (int) (voltageDiff / voltageIncrement);
            //graph every nth point based on divisor = Total points to graph
            int criticalPoints = (int) Math.ceil((float)activeDataList.size()/300);
            if (criticalPoints < 1) {
                criticalPoints = 1;
            }

            //TODO testing
            criticalPoints = 10;

            Log.d("DEBUGGING", "Critical: " + Integer.toString(criticalPoints));

            int start;
            int stop;
            int delta;
            if (i+reverse % 2 == 0) { //low to high
                start = 0;
                stop = activeDataList.size();
                delta = 1;
            } else {
                start = activeDataList.size() - 1;
                stop = -1;
                delta = -1;
            }

            ArrayList<DataPoint> dataPointsToGraph = new ArrayList<>();
            float movingAverage = 0;
            int numPointsInAverage = 0;
            int windowWidth = 10;

            for (int j = start ; j != stop; j += delta) {
                float current = (float)((activeDataList.get(j)) * (3.3/4096) / gainResistor * 1000000);
                activeCurrentList.add(new Pair<>(currentVoltage, current));

                if (numPointsInAverage < windowWidth) {
                    numPointsInAverage += 1;
                    movingAverage = (movingAverage*(((float)numPointsInAverage-1)/numPointsInAverage)) + ((1f / numPointsInAverage) * current);
                    activeSmoothedCurrentList.add(new Pair<Float, Float>(currentVoltage, movingAverage));
                } else {
                    if (start == 0) {
                        movingAverage = movingAverage + ((1f / windowWidth) * current) -
                                (activeCurrentList.get(j - windowWidth).second * ((1f / windowWidth)));
                    } else {
                        movingAverage = movingAverage + ((1f / windowWidth) * current) -
                                (activeCurrentList.get(start-j-windowWidth).second * ((1f / windowWidth)));
                    }
                    activeSmoothedCurrentList.add(new Pair<Float, Float>(currentVoltage, movingAverage));
                }



                if (j % criticalPoints == 0) {
                    dataPointsToGraph.add(new DataPoint(currentVoltage, movingAverage));
                }

                currentVoltage += voltageIncrement;
            }

            currentList.add(activeCurrentList);
            smoothedCurrentList.add(activeSmoothedCurrentList);



            DataPoint[] toPublish = dataPointsToGraph.toArray(new DataPoint[dataPointsToGraph.size()]);
            publishProgress(toPublish);
            Log.d("DEBUGGING", graphedData.toString());
            Log.d("DEBUGGING", dataPointsToGraph.toString());
            graphedData.add(dataPointsToGraph);
        }


        ArrayList<Float> smoothedCurrentToPrint = new ArrayList<>();
        int increment = 10;
        for (int i=0; i<smoothedCurrentList.size(); i+=increment) {
            ArrayList<Pair<Float, Float>> activeSmoothedCurrentList = smoothedCurrentList.get(i);
            //TODO make sure extrema aren't too close together
            for (int j=increment; j<activeSmoothedCurrentList.size()-increment; j++) {
                float currentValue = activeSmoothedCurrentList.get(j).second;
                float prevValue = activeSmoothedCurrentList.get(j-increment).second;
                float nextValue = activeSmoothedCurrentList.get(j+increment).second;
                //check for max
                if (currentValue >= prevValue && currentValue >= nextValue) {
                    Log.d("DEBUGGING", "MAX FOUND: " + Float.toString(currentValue));
                    Log.d("DEBUGGING", "VOLTAGE: " + Float.toString(activeSmoothedCurrentList.get(j).first));

                }
                //check for min
                if (currentValue <= prevValue && currentValue <= nextValue) {
                    Log.d("DEBUGGING", "MIN FOUND: " + Float.toString(currentValue));
                    Log.d("DEBUGGING", "VOLTAGE: " + Float.toString(activeSmoothedCurrentList.get(j).first));
                }
                smoothedCurrentToPrint.add(currentValue);
            }
        }

        //Log.d("DEBUGGING", smoothedCurrentToPrint.subList(0,400).toString());
        //Log.d("DEBUGGING", smoothedCurrentToPrint.subList(400,800).toString());
        //Log.d("DEBUGGING", smoothedCurrentToPrint.subList(800,1200).toString());


        return null;
    }

    @Override
    protected void onProgressUpdate(DataPoint... dataPoints) {
        LineGraphSeries<DataPoint> lineGraphSeriesToGraph = new LineGraphSeries<>(dataPoints);
        Log.d("DEBUGGING", "Graphing Data!");
        graph.addSeries(lineGraphSeriesToGraph);
        graph.getViewport().setScalable(true);




        progressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //String x = DbHelper.packGraphData(graphedData);


        time = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        String dateString = sdf.format(time);
        TextView dateTextView = (TextView) activity.findViewById(R.id.results_date_text);
        dateTextView.setText(dateString);

        Button saveDataButton = (Button) activity.findViewById(R.id.save_data_button);
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData saveDataThread = new SaveData();
                saveDataThread.run();
            }
        });

        saveDataButton.setVisibility(View.VISIBLE);

    }

    private class SaveData implements Runnable {

        public void run() {
            DbHelper dbHelper = new DbHelper(activity.getBaseContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String latitude = "100.1";
            String longitude = "100.1";

            String concentration = "11.2";

            ContentValues values = new ContentValues();
            values.put(DbHelper.COL_DATE, Long.toString(time));
            values.put(DbHelper.COL_TEST_TYPE, "Phosphate");
            values.put(DbHelper.COL_LAT, latitude);
            values.put(DbHelper.COL_LONG, longitude);
            values.put(DbHelper.COL_PEAK_VALUES, "Peak Values");
            values.put(DbHelper.COL_CONCENTRATION, concentration);

            db.insertOrThrow(DbHelper.TABLE_NAME, null, values);

            Log.d("DEBUGGING", "THREAD FINISHED");
        }

    }
}
