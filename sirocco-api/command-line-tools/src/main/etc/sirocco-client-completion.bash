#!/usr/env/bash
#
#
# SIROCCO
# Copyright (C) 2013  France Telecom
# Contact: sirocco@ow2.org
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
# USA
#
#

_find_command()
{
	for ((i=1; i < ${#COMP_WORDS[@]}; i++)); do
		word="${COMP_WORDS[$i]}" 
		case "$word" in
        	 -*)
			;;
		 *)
			echo "$word"
			return;
			;;
		esac
	done
}


_sirocco_client() 
{
    local cur prev opts base
    COMPREPLY=()
    cur="${COMP_WORDS[COMP_CWORD]}"
    #prev="${COMP_WORDS[1]}"

    command=$(_find_command)

    commands="-debug provider-list provider-show provider-delete provider-create"
    commands="${commands} provider-account-list provider-account-create provider-account-delete"
    commands="${commands} provider-location-list provider-location-add"
    commands="${commands} tenant-list tenant-create tenant-delete tenant-show tenant-account-list tenant-account-add tenant-account-remove tenant-user-add tenant-user-remove tenant-user-list"
    commands="${commands} user-list user-create user-delete user-show"
    commands="${commands} provider-profile-list user-create provider-profile-create provider-profile-metadata-add config-set"

    case "${command}" in
	provider-create)
	    local opts="-name -description -properties -endpoint -api"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;
	provider-list | provider-profile-list)
	    local opts=""
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;
	provider-show )
	    local opts="-"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;
    tenant-create)
	    local opts="-name -description -properties"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;    
    tenant-account-add | tenant-account-remove)
	    local opts="-tenantId -accountId"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;
    tenant-user-add | tenant-user-remove)
	    local opts="-tenantId -userId"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;          
    provider-location-add)    
    	local opts="-providerId -iso3166_1 -iso3166_2 -country -region -city"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;  
    provider-profile-metadata-add)    
    	local opts="-profileId -metadata"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;          
    provider-account-create) 
    	local opts="-providerId -clientId -clientSecret -name -description -properties"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;  
    provider-profile-create) 
    	local opts="-type -connectorClass -metadata -description"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;  
    provider-account-delete | provider-account-list)
    	local opts="-providerId"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;  
    user-create)
	    local opts="-username -password -email -firstname -lastname"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;   
    config-set)
	    local opts="-key -value"
	    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
            return 0
            ;;           
    *)
        ;;         	   
    esac

   COMPREPLY=($(compgen -W "${commands}" -- ${cur}))  
   return 0
}
complete -F _sirocco_client sirocco-client
