// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package simple_example

import (
	"fmt"
	"net/http"
	"testing"
	"time"

	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/gcloud"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/tft"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/utils"
	"github.com/stretchr/testify/assert"
)

func TestSimpleExample(t *testing.T) {

	example := tft.NewTFBlueprintTest(t)

	example.DefineVerify(func(assert *assert.Assertions) {
		projectID := example.GetTFSetupStringOutput("project_id")
		gcloudArgs := gcloud.WithCommonArgs([]string{"--project", projectID})

		// Check if the resource bucket exists
		resourceBucketName := example.GetStringOutput("bucket_name")
		resourceStorage := gcloud.Run(t, fmt.Sprintf("storage buckets describe gs://%s --format=json", resourceBucketName), gcloudArgs)
		assert.NotEmpty(resourceStorage)

		// Check if the blue MIG is active
		blueMigSelflink := example.GetStringOutput("blue_mig_self_link")
		blueMig := gcloud.Run(t, fmt.Sprintf("compute instance-groups managed describe %s --region us-central1 --format=json", blueMigSelflink), gcloudArgs)
		assert.True(blueMig.Get("status.isStable").Bool(), "expected blue MIG to be active")
		assert.True(blueMig.Get("status.versionTarget.isReached").Bool(), "expected blue MIG to be serving the latest version")

		// Check if the green MIG is active
		greenMigSelflink := example.GetStringOutput("green_mig_self_link")
		greenMig := gcloud.Run(t, fmt.Sprintf("compute instance-groups managed describe %s --region us-central1 --format=json", greenMigSelflink), gcloudArgs)
		assert.True(greenMig.Get("status.isStable").Bool(), "expected green MIG to be active")
		assert.True(greenMig.Get("status.versionTarget.isReached").Bool(), "expected green MIG to be serving the latest version")

		bludMigLoadbalancerIp := example.GetStringOutput("blue_mig_load_balancer_ip")
		greenMigLoadbalancerIp := example.GetStringOutput("green_mig_load_balancer_ip")

		// Check if the blue MIG load balancer is serving
		isBlueMigServing := func() (bool, error) {
			resp, err := http.Get(bludMigLoadbalancerIp)
			if err != nil || resp.StatusCode != 200 {
				// retry if err or status not 200
				return true, nil
			}
			return false, nil
		}
		utils.Poll(t, isBlueMigServing, 10, time.Minute*2)

		// Check if the green MIG load balancer is serving
		isGreenMigServing := func() (bool, error) {
			resp, err := http.Get(greenMigLoadbalancerIp)
			if err != nil || resp.StatusCode != 200 {
				// retry if err or status not 200
				return true, nil
			}
			return false, nil
		}
		utils.Poll(t, isGreenMigServing, 10, time.Minute*2)

	})
	example.Test()
}
