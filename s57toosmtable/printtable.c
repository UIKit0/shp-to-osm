/* Copyright 2012 Malcolm Herring
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * For a copy of the GNU General Public License, see <http://www.gnu.org/licenses/>.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>


#include "s57obj.h"
#include "s57att.h"
#include "s57val.h"

int main (int argc, const char * argv[]) {

	FILE * f = fopen("../data/s57_to_osm_objects.csv","wt");
	fprintf(f, "s57code,osm_name\n");
	for(int i = 0; i < OBJSIZ; i++)
	{
		char *type = decodeType(i);
		if( strlen(type) > 0 )
			fprintf(f, "%d, %s\n",i, decodeType(i));
	}
	fclose(f);

	f = fopen("../data/s57_to_osm_attrs.csv","wt");
	fprintf(f, "s57code|osm_attr|s57enum|osm_str\n");
	for( Attl_t attl = 0; attl < ATTSIZ; attl++)
	{
		char *osm_attr =  decodeAttribute( attl );
		if ( osm_attr != NULL && strlen (osm_attr) > 0)
		{
			fprintf(f,"%d|%s", attl , osm_attr );
			s57key_t * key = findKey( attl );
			if ( key != NULL )
			{
				s57val_t *val = key->val;
				if ( val  != NULL )
				{
					for (int idx = 0 ; val[idx].val != NULL ; idx++ )
					{
						if ( strlen (val[idx].val ) > 0 )
							fprintf(f,"|%d|%s", val[idx].atvl , val[idx].val );
					}
				}
			}
			fprintf(f,"\n");
		}
	}
	fclose(f);

	printf("Done\n");
  
}
