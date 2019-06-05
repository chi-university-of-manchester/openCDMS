/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.psygrid.neden.patches.v1_1_18;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch32 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		int documentCount = ds.numDocuments();
		Document opcritDoc = null;
		boolean opcritDocFound = false;

		for(int docIndex = 0; docIndex < documentCount; docIndex++){

			opcritDoc = ds.getDocument(docIndex);
			String name = opcritDoc.getName();
			if(name.equals("Opcrit Data Entry Sheet")){
				opcritDocFound = true;
				break;
			}
		}

		if(opcritDocFound){

			StringBuffer xmlImportMapping = new StringBuffer("<importmapping>" +
					"<csv_startingrow>1</csv_startingrow>" +
					"<mapping><document_entry>2</document_entry><source_entry>2</source_entry></mapping>" +
					"<mapping><document_entry>3</document_entry><source_entry>3</source_entry></mapping>" +
					"<mapping><document_entry>4</document_entry><source_entry>4</source_entry></mapping>" +
					"<mapping><document_entry>5</document_entry><source_entry>5</source_entry></mapping>" +
					"<mapping><document_entry>6</document_entry><source_entry>6</source_entry></mapping>" +
					"<mapping><document_entry>7</document_entry><source_entry>7</source_entry></mapping>" +
					"<mapping><document_entry>8</document_entry><source_entry>8</source_entry></mapping>" +
					"<mapping><document_entry>9</document_entry><source_entry>9</source_entry></mapping>" +
					"<mapping><document_entry>10</document_entry><source_entry>10</source_entry></mapping>" +
					"<mapping><document_entry>11</document_entry><source_entry>11</source_entry></mapping>" +
					"<mapping><document_entry>12</document_entry><source_entry>12</source_entry></mapping>" +
					"<mapping><document_entry>13</document_entry><source_entry>13</source_entry></mapping>" +
					"<mapping><document_entry>14</document_entry><source_entry>14</source_entry></mapping>" +
					"<mapping><document_entry>15</document_entry><source_entry>15</source_entry></mapping>" +
					"<mapping><document_entry>16</document_entry><source_entry>16</source_entry></mapping>" +
					"<mapping><document_entry>17</document_entry><source_entry>17</source_entry></mapping>" +
					"<mapping><document_entry>18</document_entry><source_entry>18</source_entry></mapping>" +
					"<mapping><document_entry>19</document_entry><source_entry>19</source_entry></mapping>" +
					"<mapping><document_entry>20</document_entry><source_entry>20</source_entry></mapping>" +
					"<mapping><document_entry>21</document_entry><source_entry>21</source_entry></mapping>" +
					"<mapping><document_entry>22</document_entry><source_entry>22</source_entry></mapping>" +
					"<mapping><document_entry>23</document_entry><source_entry>23</source_entry></mapping>" +
					"<mapping><document_entry>24</document_entry><source_entry>24</source_entry></mapping>" +
					"<mapping><document_entry>25</document_entry><source_entry>25</source_entry></mapping>" +
					"<mapping><document_entry>26</document_entry><source_entry>26</source_entry></mapping>" +
					"<mapping><document_entry>27</document_entry><source_entry>27</source_entry></mapping>" +
					"<mapping><document_entry>28</document_entry><source_entry>28</source_entry></mapping>" +
					"<mapping><document_entry>29</document_entry><source_entry>29</source_entry></mapping>" +
					"<mapping><document_entry>30</document_entry><source_entry>30</source_entry></mapping>" +
					"<mapping><document_entry>31</document_entry><source_entry>31</source_entry></mapping>" +
					"<mapping><document_entry>32</document_entry><source_entry>32</source_entry></mapping>" +
					"<mapping><document_entry>33</document_entry><source_entry>33</source_entry></mapping>" +
					"<mapping><document_entry>34</document_entry><source_entry>34</source_entry></mapping>" +
					"<mapping><document_entry>35</document_entry><source_entry>35</source_entry></mapping>" +
					"<mapping><document_entry>36</document_entry><source_entry>36</source_entry></mapping>" +
					"<mapping><document_entry>37</document_entry><source_entry>37</source_entry></mapping>" +
					"<mapping><document_entry>38</document_entry><source_entry>38</source_entry></mapping>" +
					"<mapping><document_entry>39</document_entry><source_entry>39</source_entry></mapping>" +
					"<mapping><document_entry>40</document_entry><source_entry>40</source_entry></mapping>" +
					"<mapping><document_entry>41</document_entry><source_entry>41</source_entry></mapping>" +
					"<mapping><document_entry>42</document_entry><source_entry>42</source_entry></mapping>" +
					"<mapping><document_entry>43</document_entry><source_entry>43</source_entry></mapping>" +
					"<mapping><document_entry>44</document_entry><source_entry>44</source_entry></mapping>" +
					"<mapping><document_entry>45</document_entry><source_entry>45</source_entry></mapping>" +
					"<mapping><document_entry>46</document_entry><source_entry>46</source_entry></mapping>" +
					"<mapping><document_entry>47</document_entry><source_entry>47</source_entry></mapping>" +
					"<mapping><document_entry>48</document_entry><source_entry>48</source_entry></mapping>" +
					"<mapping><document_entry>49</document_entry><source_entry>49</source_entry></mapping>" +
					"<mapping><document_entry>50</document_entry><source_entry>50</source_entry></mapping>" +
					"<mapping><document_entry>51</document_entry><source_entry>51</source_entry></mapping>" +
					"<mapping><document_entry>52</document_entry><source_entry>52</source_entry></mapping>" +
					"<mapping><document_entry>53</document_entry><source_entry>53</source_entry></mapping>" +
					"<mapping><document_entry>54</document_entry><source_entry>54</source_entry></mapping>" +
					"<mapping><document_entry>56</document_entry><source_entry>55</source_entry></mapping>" +
					"<mapping><document_entry>57</document_entry><source_entry>56</source_entry></mapping>" +
					"<mapping><document_entry>58</document_entry><source_entry>57</source_entry></mapping>" +
					"<mapping><document_entry>59</document_entry><source_entry>58</source_entry></mapping>" +
					"<mapping><document_entry>60</document_entry><source_entry>59</source_entry></mapping>" +
					"<mapping><document_entry>61</document_entry><source_entry>60</source_entry></mapping>" +
					"<mapping><document_entry>62</document_entry><source_entry>61</source_entry></mapping>" +
					"<mapping><document_entry>63</document_entry><source_entry>62</source_entry></mapping>" +
					"<mapping><document_entry>64</document_entry><source_entry>63</source_entry></mapping>" +
					"<mapping><document_entry>65</document_entry><source_entry>64</source_entry></mapping>" +
					"<mapping><document_entry>66</document_entry><source_entry>65</source_entry></mapping>" +
					"<mapping><document_entry>67</document_entry><source_entry>66</source_entry></mapping>" +
					"<mapping><document_entry>68</document_entry><source_entry>67</source_entry></mapping>" +
					"<mapping><document_entry>69</document_entry><source_entry>68</source_entry></mapping>" +
					"<mapping><document_entry>70</document_entry><source_entry>69</source_entry></mapping>" +
					"<mapping><document_entry>71</document_entry><source_entry>70</source_entry></mapping>" +
					"<mapping><document_entry>72</document_entry><source_entry>71</source_entry></mapping>" +
					"<mapping><document_entry>73</document_entry><source_entry>72</source_entry></mapping>" +
					"<mapping><document_entry>74</document_entry><source_entry>73</source_entry></mapping>" +
					"<mapping><document_entry>75</document_entry><source_entry>74</source_entry></mapping>" +
					"<mapping><document_entry>76</document_entry><source_entry>75</source_entry></mapping>" +
					"<mapping><document_entry>77</document_entry><source_entry>76</source_entry></mapping>" +
					"<mapping><document_entry>78</document_entry><source_entry>77</source_entry></mapping>" +
					"<mapping><document_entry>79</document_entry><source_entry>78</source_entry></mapping>" +
					"<mapping><document_entry>80</document_entry><source_entry>79</source_entry></mapping>" +
					"<mapping><document_entry>81</document_entry><source_entry>80</source_entry></mapping>" +
					"<mapping><document_entry>82</document_entry><source_entry>81</source_entry></mapping>" +
					"<mapping><document_entry>83</document_entry><source_entry>82</source_entry></mapping>" +
					"<mapping><document_entry>84</document_entry><source_entry>83</source_entry></mapping>" +
					"<mapping><document_entry>85</document_entry><source_entry>84</source_entry></mapping>" +
					"<mapping><document_entry>86</document_entry><source_entry>85</source_entry></mapping>" +
					"<mapping><document_entry>87</document_entry><source_entry>86</source_entry></mapping>" +
					"<mapping><document_entry>88</document_entry><source_entry>87</source_entry></mapping>" +
					"<mapping><document_entry>89</document_entry><source_entry>88</source_entry></mapping>" +
					"<mapping><document_entry>90</document_entry><source_entry>89</source_entry></mapping>" +
					"<mapping><document_entry>91</document_entry><source_entry>90</source_entry></mapping>" +
					"<mapping><document_entry>92</document_entry><source_entry>91</source_entry></mapping>" +
					"</importmapping>");

			String mappingString = xmlImportMapping.toString();
			opcritDoc.setIsImportEnabled(true);
			opcritDoc.setImportMappingString(mappingString);

		}else{
			throw new RuntimeException("The opcrit document could not be found in the returned NEDEN dataset.");
		}


	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Add import mapping to Oprit document";
	}

}
