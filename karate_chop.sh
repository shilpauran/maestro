#!/usr/bin/env bash

usage()
{
  echo ""
  echo "Usage: $0 [ -l|-p ] [ -i|-e ] [ -s SUFFIX ] [ -r REGION ] JWT"
  echo ""
  echo "Options:"
  echo "    Execution:"
  echo "      -l                    Execute tests in linear mode (default)"
  echo "      -p                    Execute tests in parallel mode"
  echo "    Route:"
  echo "      -i                    Use internal route (default)"
  echo "      -e                    Use external route"
  echo "    -s SUFFIX               The application name suffix (default: none), i.e.: 'acce' on tax-service-acce"
  echo "    -r REGION               Application region (default: sap), e.g.: sap, eu10, us20"
  echo "    JWT                     JWT to be used with the HTTP calls"
  exit 2
}

set_variable()
{
  local varname=$1
  shift
  if [ -z "${!varname}" ]; then
    eval "$varname=\"$@\""
  else
    echo "Error: $varname already set"
    usage
  fi
}

unset JWT SUFFIX TYPE ROUTE REGION

while getopts :plies:r: opt
do	case "$opt" in
    p)  set_variable TYPE "KarateRunnerParallel";;
    l)  set_variable TYPE "KarateRunnerLinear";;
    i)  set_variable ROUTE "internal";;
    e)  set_variable ROUTE "external";;
    s)  set_variable SUFFIX "$OPTARG";;
    r)  set_variable REGION "$OPTARG";;
    \?) echo "Invalid option: -$OPTARG" 1>&2; usage;;
    :)  echo "Invalid option: -$OPTARG requires an argument" 1>&2; usage;;
    esac
done
shift $((OPTIND-1))

JWT="$@"

[ -z "$JWT" ] && usage

if [ -z "$TYPE" ]; then
  TYPE="KarateRunnerLinear"
fi

if [ -z "$ROUTE" ]; then
  ROUTE="internal"
fi
if [ "$ROUTE" == "internal" ]; then
  ROUTE="internal."
else
  ROUTE=""
fi

if ! [ -z "$SUFFIX" ]; then
  SUFFIX="-$SUFFIX"
fi

if [ -z "$REGION" ]; then
  REGION="sap."
else
  REGION="$REGION."
fi

export TXS_JWT=$JWT
export TXS_TAX_ATTRIBUTES_DETERMINATION_URL=https://tax-service"$SUFFIX"."$ROUTE"cfapps."$REGION"hana.ondemand.com/determination
export TXS_TAX_MAESTRO_URL=https://tax-service"$SUFFIX"."$ROUTE"cfapps."$REGION"hana.ondemand.com/tax
export TXS_TAX_CONTENT_URL=https://tax-service"$SUFFIX"."$ROUTE"cfapps."$REGION"hana.ondemand.com/content
export TXS_TAX_CALCULATION_SERVICE_URL=https://tax-service"$SUFFIX"."$ROUTE"cfapps."$REGION"hana.ondemand.com/calculation
export TXS_TAX_DESTINATION_CONFIGURATION_URL=https://tax-service"$SUFFIX"."$ROUTE"cfapps."$REGION"hana.ondemand.com/destination
export TXS_PRIMARY_URL=$TXS_TAX_MAESTRO_URL

env | grep TXS_

echo "mvn clean test -Dtest=$TYPE -DfailIfNoTests=false"
mvn clean test -Dtest=$TYPE -DfailIfNoTests=false
