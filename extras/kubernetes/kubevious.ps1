# Copyright (C) 2022 -  Juergen Zimmermann, Hochschule Karlsruhe
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

# https://docs.microsoft.com/en-us/powershell/scripting/developer/cmdlet/approved-verbs-for-windows-powershell-commands?view=powershell-7

# Aufruf:   .\kubevious.ps1

# "Param" muss in der 1. Zeile sein
Param (
    [string]$op = ''
)

Set-StrictMode -Version Latest

$versionMinimum = [Version]'7.4.0'
$versionCurrent = $PSVersionTable.PSVersion
if ($versionMinimum -gt $versionCurrent) {
    throw "PowerShell $versionMinimum statt $versionCurrent erforderlich"
}

# Titel setzen
$host.ui.RawUI.WindowTitle = 'kubevious'

$release = 'kubevious'
$namespace = 'kubevious'

function Dashboard {
    $port = '7077'
    Write-Output ''
    Write-Output "Aufruf in einem Webbrowser: http://localhost:$port"
    Write-Output ''
    kubectl port-forward service/kubevious-ui-clusterip ${port}:80 --namespace $namespace
}

function Install {
    $version = '1.1.2'
    helm repo add kubevious https://helm.kubevious.io
    helm repo update
    kubectl create namespace $namespace
    helm install $release kubevious/kubevious --version $version --namespace $namespace
    Write-Output ''
    Write-Output "Überprüfen, ob die Pods im Namespace '$namespace' erfolgreich gestartet sind"
    Write-Output ''
}

function Uninstall {
    helm uninstall $release --namespace $namespace
    kubectl delete namespace $namespace
}

switch ($op) {
    '' { Dashboard }
    'install' { Install }
    'uninstall' { Uninstall }
    default { Write-Output 'Aufruf: kubevious [install|uninstall]' }
}
